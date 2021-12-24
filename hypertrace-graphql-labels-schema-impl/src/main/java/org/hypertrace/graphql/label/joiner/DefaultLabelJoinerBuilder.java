package org.hypertrace.graphql.label.joiner;

import static io.reactivex.rxjava3.core.Single.zip;
import static java.util.function.Function.identity;
import static org.hypertrace.graphql.label.schema.LabeledEntities.ENTITY_TYPE_ARGUMENT_NAME;
import static org.hypertrace.graphql.label.schema.LabeledEntities.LABELED_ENTITIES_QUERY_NAME;

import graphql.schema.DataFetchingFieldSelectionSet;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.core.graphql.common.request.ContextualRequestBuilder;
import org.hypertrace.core.graphql.common.request.FilterRequestBuilder;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequestBuilder;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterOperatorType;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterType;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.LimitArgument;
import org.hypertrace.core.graphql.common.utils.CollectorUtils;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;
import org.hypertrace.graphql.entity.dao.EntityDao;
import org.hypertrace.graphql.entity.request.EdgeSetGroupRequest;
import org.hypertrace.graphql.entity.request.EntityLabelRequest;
import org.hypertrace.graphql.entity.request.EntityRequest;
import org.hypertrace.graphql.entity.schema.EntityResultSet;
import org.hypertrace.graphql.label.dao.LabelDao;
import org.hypertrace.graphql.label.dao.LabelResponseConverter;
import org.hypertrace.graphql.label.schema.Label;
import org.hypertrace.graphql.label.schema.LabelResultSet;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.request.MetricRequest;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;

@Slf4j
class DefaultLabelJoinerBuilder implements LabelJoinerBuilder {

  private static final Integer DEFAULT_LIMIT = 10;
  private static final int ZERO_OFFSET = 0;

  private static final SelectionQuery RESULT_SET_LABELED_ENTITIES_SELECTION_QUERY =
      SelectionQuery.builder()
          .selectionPath(List.of(ResultSet.RESULT_SET_RESULTS_NAME, LABELED_ENTITIES_QUERY_NAME))
          .build();

  private final ContextualRequestBuilder requestBuilder;
  private final ResultSetRequestBuilder resultSetRequestBuilder;
  private final FilterRequestBuilder filterRequestBuilder;
  private final LabelDao labelDao;
  private final LabelResponseConverter responseConverter;
  private final GraphQlSelectionFinder selectionFinder;
  private final EntityDao entityDao;

  @Inject
  DefaultLabelJoinerBuilder(
      ContextualRequestBuilder requestBuilder,
      ResultSetRequestBuilder resultSetRequestBuilder,
      FilterRequestBuilder filterRequestBuilder,
      LabelDao labelDao,
      EntityDao entityDao,
      LabelResponseConverter responseConverter,
      GraphQlSelectionFinder selectionFinder) {
    this.requestBuilder = requestBuilder;
    this.resultSetRequestBuilder = resultSetRequestBuilder;
    this.filterRequestBuilder = filterRequestBuilder;
    this.labelDao = labelDao;
    this.entityDao = entityDao;
    this.responseConverter = responseConverter;
    this.selectionFinder = selectionFinder;
  }

  @Override
  public Single<LabelJoiner> build(GraphQlRequestContext context) {
    return buildContextualRequest(context)
        .map(request -> new DefaultLabelJoiner(context, request, null));
  }

  @Override
  public Single<LabelJoiner> build(
      GraphQlRequestContext context, DataFetchingFieldSelectionSet selectionSet) {
    return zip(
        buildContextualRequest(context),
        buildLabeledEntitiesRequestIfPresentInResultSet(selectionSet),
        (contextualRequest, labeledEntitiesRequests) ->
            new DefaultLabelJoiner(context, contextualRequest, labeledEntitiesRequests));
  }

  private Single<ContextualRequest> buildContextualRequest(GraphQlRequestContext context) {
    return Single.just(this.requestBuilder.build(context));
  }

  private Single<List<LabeledEntitiesRequest>> buildLabeledEntitiesRequestIfPresentInResultSet(
      DataFetchingFieldSelectionSet selectionSet) {
    return Single.just(
        this.selectionFinder
            .findSelections(selectionSet, RESULT_SET_LABELED_ENTITIES_SELECTION_QUERY)
            .map(
                selectedField ->
                    new DefaultLabeledEntitiesRequest(
                        (String) selectedField.getArguments().get(ENTITY_TYPE_ARGUMENT_NAME),
                        (Integer)
                            selectedField
                                .getArguments()
                                .getOrDefault(LimitArgument.ARGUMENT_NAME, DEFAULT_LIMIT),
                        selectedField.getSelectionSet()))
            .collect(Collectors.toList()));
  }

  @AllArgsConstructor
  private class DefaultLabelJoiner implements LabelJoiner {
    private final GraphQlRequestContext context;
    private final ContextualRequest resultSetRequest;
    private final List<LabeledEntitiesRequest> labeledEntitiesRequests;

    @Override
    public <T> Single<Map<T, LabelResultSet>> joinLabels(
        Collection<T> joinSources, LabelIdGetter<T> labelIdGetter) {
      return labelDao
          .getLabels(resultSetRequest)
          .flatMap(labelResultSet -> buildLabelMap(labelResultSet.results()))
          .flatMap(labelsMap -> getLabelResultSetMap(joinSources, labelIdGetter, labelsMap));
    }

    @Override
    public Single<LabelResultSet> joinLabelsWithEntities() {
      return labelDao.getLabels(resultSetRequest).flatMap(this::getUpdatedLabelResultSet);
    }

    private Single<LabelResultSet> getUpdatedLabelResultSet(LabelResultSet labelResultSet) {
      return getUpdatedLabels(labelResultSet).flatMap(responseConverter::convert);
    }

    private Single<List<Label>> getUpdatedLabels(LabelResultSet labelResultSet) {
      return Observable.fromIterable(labelResultSet.results())
          .flatMapSingle(this::getUpdatedLabel)
          .collect(Collectors.toList());
    }

    private Single<Label> getUpdatedLabel(Label label) {
      return getEntityResultSetMap(label)
          .flatMap(entityResultMap -> responseConverter.convertLabel(label, entityResultMap));
    }

    private Single<Map<String, EntityResultSet>> getEntityResultSetMap(Label label) {
      String labelId = label.id();
      return Observable.fromIterable(labeledEntitiesRequests)
          .flatMapSingle(request -> getEntityResultSetMapEntry(labelId, request))
          .collect(CollectorUtils.immutableMapEntryCollector());
    }

    private Single<Map.Entry<String, EntityResultSet>> getEntityResultSetMapEntry(
        String labelId, LabeledEntitiesRequest request) {
      return buildEntityRequestForType(
              context, request.entityType(), labelId, request.limit(), request.selectionSet())
          .flatMap(entityDao::getEntities)
          .map(resultSet -> Map.entry(request.entityType(), resultSet));
    }

    private Single<Map<String, Label>> buildLabelMap(List<Label> results) {
      return Single.just(
          results.stream().collect(Collectors.toUnmodifiableMap(Label::id, identity())));
    }

    private <T> Single<Map<T, LabelResultSet>> getLabelResultSetMap(
        Collection<T> joinSources, LabelIdGetter<T> labelIdGetter, Map<String, Label> labelMap) {
      return Observable.fromIterable(joinSources)
          .flatMapSingle(source -> buildLabelResultSetMapEntry(source, labelIdGetter, labelMap))
          .collect(CollectorUtils.immutableMapEntryCollector());
    }

    private <T> Single<Map.Entry<T, LabelResultSet>> buildLabelResultSetMapEntry(
        T source, LabelIdGetter<T> labelIdGetter, Map<String, Label> labelMap) {
      return labelIdGetter
          .getLabelIds(source)
          .flatMap(labelIds -> filterLabels(labelIds, labelMap))
          .flatMap(responseConverter::convert)
          .map(labelResultSet -> Map.entry(source, labelResultSet));
    }

    private Single<List<Label>> filterLabels(List<String> labelIds, Map<String, Label> labelMap) {
      return Single.just(
          labelIds.stream()
              .map(labelId -> getLabelFromMap(labelId, labelMap))
              .filter(Objects::nonNull)
              .collect(Collectors.toUnmodifiableList()));
    }

    private Label getLabelFromMap(String labelId, Map<String, Label> labelMap) {
      Label label = labelMap.get(labelId);
      if (label == null) {
        log.warn("Label config doesn't exist for label id {}", labelId);
      }
      return label;
    }

    private Single<EntityRequest> buildEntityRequestForType(
        GraphQlRequestContext context,
        String entityType,
        String labelId,
        int limit,
        DataFetchingFieldSelectionSet selectionSet) {
      return buildLabelIdFilter(context, entityType, labelId)
          .flatMap(
              filterArguments ->
                  buildEntityRequest(context, entityType, limit, filterArguments, selectionSet));
    }

    private Single<EntityRequest> buildEntityRequest(
        GraphQlRequestContext context,
        String entityType,
        int entityIdsToFilterSize,
        List<AttributeAssociation<FilterArgument>> filterArguments,
        DataFetchingFieldSelectionSet selectionSet) {
      boolean fetchTotal =
          selectionFinder
                  .findSelections(
                      selectionSet, SelectionQuery.namedChild(ResultSet.RESULT_SET_TOTAL_NAME))
                  .count()
              > 0;
      return resultSetRequestBuilder
          .build(
              context,
              entityType,
              entityIdsToFilterSize,
              ZERO_OFFSET,
              new InstantTimeRange(),
              List.<AttributeAssociation<AggregatableOrderArgument>>of(), // Order does not matter
              filterArguments,
              selectionSet.getFields().stream(),
              Optional.empty())
          .map(request -> new DefaultEntityRequest(entityType, request, fetchTotal));
    }

    private Single<List<AttributeAssociation<FilterArgument>>> buildLabelIdFilter(
        GraphQlRequestContext context, String entityScope, String labelId) {
      return filterRequestBuilder.build(context, entityScope, Set.of(new LabelIdFilter(labelId)));
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultEntityRequest implements EntityRequest {
    String entityType;
    ResultSetRequest<AggregatableOrderArgument> resultSetRequest;
    List<MetricRequest> metricRequests = Collections.emptyList(); // Only support attributes for now
    boolean fetchTotal;
    EdgeSetGroupRequest incomingEdgeRequests = new EmptyEdgeSetGroupRequest();
    EdgeSetGroupRequest outgoingEdgeRequests = new EmptyEdgeSetGroupRequest();
    boolean includeInactive = true; // When joining we want the entity regardless of time range
    Optional<EntityLabelRequest> labelRequest = Optional.empty();
  }

  @Value
  @Accessors(fluent = true)
  private static class EmptyEdgeSetGroupRequest implements EdgeSetGroupRequest {
    Set<String> entityTypes = Collections.emptySet();
    Collection<AttributeRequest> attributeRequests = Collections.emptyList();
    Collection<MetricAggregationRequest> metricAggregationRequests = Collections.emptyList();
    AttributeRequest neighborIdAttribute = null;
    AttributeRequest neighborTypeAttribute = null;

    @Override
    public Single<EntityRequest> buildNeighborRequest(
        String entityType, Collection<String> neighborIds) {
      return Single.error(
          new UnsupportedOperationException(
              "Does not support fetching neighbors for joined entities"));
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class InstantTimeRange implements TimeRangeArgument {
    // Bug in gateway requires this time range be larger, even though we don't care about it
    Instant startTime = Instant.now().minus(15, ChronoUnit.MINUTES);
    Instant endTime = Instant.now();
  }

  @Value
  @Accessors(fluent = true)
  private static class LabelIdFilter implements FilterArgument {
    FilterType type = FilterType.ATTRIBUTE;
    String key = "labels";
    FilterOperatorType operator = FilterOperatorType.EQUALS;
    String value;
    AttributeScope idType = null;
    String idScope = null;
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLabeledEntitiesRequest implements LabeledEntitiesRequest {
    String entityType;
    int limit;
    DataFetchingFieldSelectionSet selectionSet;
  }
}
