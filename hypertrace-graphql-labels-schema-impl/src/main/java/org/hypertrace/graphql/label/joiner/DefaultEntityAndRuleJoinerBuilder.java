package org.hypertrace.graphql.label.joiner;

import static io.reactivex.rxjava3.core.Single.zip;
import static org.hypertrace.graphql.label.schema.Label.LABELED_ENTITIES_QUERY_NAME;
import static org.hypertrace.graphql.label.schema.Label.LABEL_APPLICATION_RULES_QUERY_NAME;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterOperatorType;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterType;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.LimitArgument;
import org.hypertrace.core.graphql.common.utils.CollectorUtils;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;
import org.hypertrace.graphql.entity.dao.EntityDao;
import org.hypertrace.graphql.entity.request.EdgeSetGroupRequest;
import org.hypertrace.graphql.entity.request.EntityLabelRequest;
import org.hypertrace.graphql.entity.request.EntityRequest;
import org.hypertrace.graphql.entity.schema.EntityResultSet;
import org.hypertrace.graphql.entity.schema.argument.EntityTypeStringArgument;
import org.hypertrace.graphql.label.dao.LabelApplicationRuleDao;
import org.hypertrace.graphql.label.dao.LabelDao;
import org.hypertrace.graphql.label.dao.LabelResponseConverter;
import org.hypertrace.graphql.label.schema.Label;
import org.hypertrace.graphql.label.schema.LabelResultSet;
import org.hypertrace.graphql.label.schema.rule.LabelApplicationRule;
import org.hypertrace.graphql.label.schema.rule.LabelApplicationRuleResultSet;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.request.MetricRequest;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;

@Slf4j
class DefaultEntityAndRuleJoinerBuilder implements EntityAndRuleJoinerBuilder {

  private static final Integer DEFAULT_LIMIT = 10;
  private static final int ZERO_OFFSET = 0;

  private static final SelectionQuery RESULT_SET_LABELED_ENTITIES_SELECTION_QUERY =
      SelectionQuery.builder()
          .selectionPath(List.of(ResultSet.RESULT_SET_RESULTS_NAME, LABELED_ENTITIES_QUERY_NAME))
          .build();

  private static final SelectionQuery RESULT_SET_LABEL_APPLICATION_RULES_SELECTION_QUERY =
      SelectionQuery.builder()
          .selectionPath(
              List.of(ResultSet.RESULT_SET_RESULTS_NAME, LABEL_APPLICATION_RULES_QUERY_NAME))
          .build();

  private final LabelApplicationRuleResultSet EMPTY_LABEL_APPLICATION_RULE_RESULT_SET =
      new DefaultLabelApplicationRuleResultSet(Collections.emptyList(), 0, 0);

  private final ContextualRequestBuilder requestBuilder;
  private final ResultSetRequestBuilder resultSetRequestBuilder;
  private final FilterRequestBuilder filterRequestBuilder;
  private final LabelDao labelDao;
  private final LabelApplicationRuleDao labelApplicationRuleDao;
  private final EntityDao entityDao;
  private final LabelResponseConverter responseConverter;
  private final GraphQlSelectionFinder selectionFinder;
  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  DefaultEntityAndRuleJoinerBuilder(
      ContextualRequestBuilder requestBuilder,
      ResultSetRequestBuilder resultSetRequestBuilder,
      FilterRequestBuilder filterRequestBuilder,
      LabelDao labelDao,
      LabelApplicationRuleDao labelApplicationRuleDao,
      EntityDao entityDao,
      LabelResponseConverter responseConverter,
      GraphQlSelectionFinder selectionFinder,
      ArgumentDeserializer argumentDeserializer) {
    this.requestBuilder = requestBuilder;
    this.resultSetRequestBuilder = resultSetRequestBuilder;
    this.filterRequestBuilder = filterRequestBuilder;
    this.labelDao = labelDao;
    this.labelApplicationRuleDao = labelApplicationRuleDao;
    this.entityDao = entityDao;
    this.responseConverter = responseConverter;
    this.selectionFinder = selectionFinder;
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public Single<EntityAndRuleJoiner> build(
      GraphQlRequestContext context, DataFetchingFieldSelectionSet selectionSet) {
    return zip(
        buildContextualRequest(context),
        buildLabeledEntitiesRequestIfPresentInResultSet(selectionSet),
        buildLabelApplicationRulesRequestIfPresentInResultSet(selectionSet),
        (contextualRequest, labeledEntitiesRequests, labelApplicationRulesRequest) ->
            new DefaultEntityAndRuleJoiner(
                context, contextualRequest, labeledEntitiesRequests, labelApplicationRulesRequest));
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
                        getEntityType(selectedField),
                        getLimit(selectedField),
                        selectedField.getSelectionSet()))
            .collect(Collectors.toList()));
  }

  private Single<Optional<LabelApplicationRulesRequest>>
      buildLabelApplicationRulesRequestIfPresentInResultSet(
          DataFetchingFieldSelectionSet selectionSet) {
    return Single.just(
        this.selectionFinder
            .findSelections(selectionSet, RESULT_SET_LABEL_APPLICATION_RULES_SELECTION_QUERY)
            .findAny()
            .map(
                selectedField -> new DefaultLabelApplicationRulesRequest(getLimit(selectedField))));
  }

  private String getEntityType(SelectedField selectedField) {
    return this.argumentDeserializer
        .deserializePrimitive(selectedField.getArguments(), EntityTypeStringArgument.class)
        .orElseThrow();
  }

  private int getLimit(SelectedField selectedField) {
    return this.argumentDeserializer
        .deserializePrimitive(selectedField.getArguments(), LimitArgument.class)
        .orElse(DEFAULT_LIMIT);
  }

  @AllArgsConstructor
  private class DefaultEntityAndRuleJoiner implements EntityAndRuleJoiner {
    private final GraphQlRequestContext context;
    private final ContextualRequest contextualRequest;
    private final List<LabeledEntitiesRequest> labeledEntitiesRequests;
    private final Optional<LabelApplicationRulesRequest> labelApplicationRulesRequest;

    @Override
    public Single<LabelResultSet> joinLabelsWithEntitiesAndRules() {
      return zip(
              labelDao.getLabels(contextualRequest),
              getLabelApplicationRuleResultSet(),
              this::getUpdatedLabelResultSet)
          .flatMap(labelResultSet -> labelResultSet);
    }

    private Single<LabelApplicationRuleResultSet> getLabelApplicationRuleResultSet() {
      return labelApplicationRulesRequest
          .map(request -> labelApplicationRuleDao.getLabelApplicationRules(contextualRequest))
          .orElse(Single.just(EMPTY_LABEL_APPLICATION_RULE_RESULT_SET));
    }

    private Single<LabelResultSet> getUpdatedLabelResultSet(
        LabelResultSet labelResultSet,
        LabelApplicationRuleResultSet labelApplicationRuleResultSet) {
      return getUpdatedLabels(labelResultSet, labelApplicationRuleResultSet)
          .flatMap(responseConverter::convert);
    }

    private Single<List<Label>> getUpdatedLabels(
        LabelResultSet labelResultSet,
        LabelApplicationRuleResultSet labelApplicationRuleResultSet) {
      return Observable.fromIterable(labelResultSet.results())
          .flatMapSingle(label -> this.getUpdatedLabel(label, labelApplicationRuleResultSet))
          .collect(Collectors.toList());
    }

    private Single<Label> getUpdatedLabel(
        Label label, LabelApplicationRuleResultSet labelApplicationRuleResultSet) {
      return zip(
              getEntityResultSetMap(label),
              filterLabelApplicationRuleResultSet(label, labelApplicationRuleResultSet),
              (entityResultMap, filteredLabelApplicationRuleResultSet) ->
                  responseConverter.convertLabel(
                      label, entityResultMap, filteredLabelApplicationRuleResultSet))
          .flatMap(updatedLabel -> updatedLabel);
    }

    private Single<LabelApplicationRuleResultSet> filterLabelApplicationRuleResultSet(
        Label label, LabelApplicationRuleResultSet resultSet) {
      if (resultSet.equals(EMPTY_LABEL_APPLICATION_RULE_RESULT_SET)) {
        return Single.just(resultSet);
      }
      String labelId = label.id();
      List<LabelApplicationRule> labelApplicationRules =
          resultSet.results().stream()
              .filter(rule -> this.hasMatchingLabelId(rule, labelId))
              .collect(Collectors.toList());
      int limit = labelApplicationRulesRequest.get().limit();
      List<LabelApplicationRule> labelApplicationRuleSubList =
          labelApplicationRules.size() > limit
              ? labelApplicationRules.subList(ZERO_OFFSET, limit)
              : labelApplicationRules;
      return Single.just(
          new DefaultLabelApplicationRuleResultSet(
              labelApplicationRuleSubList,
              labelApplicationRules.size(),
              labelApplicationRuleSubList.size()));
    }

    private boolean hasMatchingLabelId(LabelApplicationRule rule, String labelId) {
      if (rule.labelApplicationRuleData().action().staticLabels() != null) {
        return rule.labelApplicationRuleData().action().staticLabels().ids().contains(labelId);
      }
      return false;
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
          .map(request -> new DefaultEntityRequest(context, entityType, request, fetchTotal));
    }

    private Single<List<AttributeAssociation<FilterArgument>>> buildLabelIdFilter(
        GraphQlRequestContext context, String entityScope, String labelId) {
      return filterRequestBuilder.build(context, entityScope, Set.of(new LabelIdFilter(labelId)));
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultEntityRequest implements EntityRequest {
    GraphQlRequestContext context;
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
    Collection<AttributeAssociation<FilterArgument>> filterArguments = Collections.emptyList();
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
    String key = null;
    AttributeExpression keyExpression = AttributeExpression.forAttributeKey("labels");
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

  @Value
  @Accessors(fluent = true)
  private static class DefaultLabelApplicationRulesRequest implements LabelApplicationRulesRequest {
    int limit;
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLabelApplicationRuleResultSet
      implements LabelApplicationRuleResultSet {
    List<LabelApplicationRule> results;
    long total;
    long count;
  }
}
