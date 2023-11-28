package org.hypertrace.graphql.entity.request;

import static io.reactivex.rxjava3.core.Single.zip;

import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Provider;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.request.FilterRequestBuilder;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;
import org.hypertrace.graphql.atttribute.scopes.HypertraceAttributeScopeString;
import org.hypertrace.graphql.entity.schema.EntityType;
import org.hypertrace.graphql.entity.schema.argument.NeighborEntityScopeArgument;
import org.hypertrace.graphql.entity.schema.argument.NeighborEntityTypeArgument;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.request.MetricAggregationRequestBuilder;

class EdgeRequestBuilder {

  private final String INCOMING_ENTITY_ID_KEY = "fromEntityId";
  private final String INCOMING_ENTITY_TYPE_KEY = "fromEntityType";
  private final String OUTGOING_ENTITY_ID_KEY = "toEntityId";
  private final String OUTGOING_ENTITY_TYPE_KEY = "toEntityType";
  private final ArgumentDeserializer argumentDeserializer;
  private final GraphQlSelectionFinder selectionFinder;
  private final MetricAggregationRequestBuilder metricAggregationRequestBuilder;
  private final FilterRequestBuilder filterRequestBuilder;
  private final AttributeRequestBuilder attributeRequestBuilder;
  // Use provider to avoid cycle
  private final Provider<NeighborEntitiesRequestBuilder> neighborEntitiesRequestBuilderProvider;

  @Inject
  EdgeRequestBuilder(
      ArgumentDeserializer argumentDeserializer,
      GraphQlSelectionFinder selectionFinder,
      MetricAggregationRequestBuilder metricAggregationRequestBuilder,
      FilterRequestBuilder filterRequestBuilder,
      AttributeRequestBuilder attributeRequestBuilder,
      Provider<NeighborEntitiesRequestBuilder> neighborEntitiesRequestBuilderProvider) {
    this.argumentDeserializer = argumentDeserializer;
    this.selectionFinder = selectionFinder;
    this.metricAggregationRequestBuilder = metricAggregationRequestBuilder;
    this.attributeRequestBuilder = attributeRequestBuilder;
    this.neighborEntitiesRequestBuilderProvider = neighborEntitiesRequestBuilderProvider;
    this.filterRequestBuilder = filterRequestBuilder;
  }

  Single<EdgeSetGroupRequest> buildIncomingEdgeRequest(
      GraphQlRequestContext context,
      TimeRangeArgument timeRange,
      Optional<String> space,
      Stream<SelectedField> edgeSetFields) {
    return this.buildEdgeRequest(context, timeRange, space, edgeSetFields, EdgeType.INCOMING);
  }

  Single<EdgeSetGroupRequest> buildOutgoingEdgeRequest(
      GraphQlRequestContext context,
      TimeRangeArgument timeRange,
      Optional<String> space,
      Stream<SelectedField> edgeSetFields) {
    return this.buildEdgeRequest(context, timeRange, space, edgeSetFields, EdgeType.OUTGOING);
  }

  private Single<EdgeSetGroupRequest> buildEdgeRequest(
      GraphQlRequestContext context,
      TimeRangeArgument timeRange,
      Optional<String> space,
      Stream<SelectedField> edgeSetFields,
      EdgeType edgeType) {
    Set<SelectedField> edgeFields = edgeSetFields.collect(Collectors.toUnmodifiableSet());
    Map<String, Set<SelectedField>> edgesSelectionsByType =
        this.getEdgesSelectionByType(edgeFields.stream());

    return zip(
        this.getNeighborIdAttribute(context, edgeType),
        this.getNeighborTypeAttribute(context, edgeType),
        this.getEntityTypeToEdgeSetRequest(context, edgeType, edgeFields),
        (neighborIdRequest, neighborTypeRequest, edgeRequests) ->
            new DefaultEdgeSetGroupRequest(
                neighborIdRequest,
                neighborTypeRequest,
                edgeRequests,
                (entityType, neighborIds) ->
                    this.neighborEntitiesRequestBuilderProvider
                        .get()
                        .buildNeighborRequest(
                            context,
                            entityType,
                            timeRange,
                            space,
                            neighborIds,
                            edgesSelectionsByType.get(entityType))));
  }

  private Single<Map<String, EdgeSetRequest>> getEntityTypeToEdgeSetRequest(
      GraphQlRequestContext context, EdgeType edgeType, Set<SelectedField> edgeFields) {
    return Observable.fromIterable(edgeFields)
        .collect(Collectors.groupingBy(this::getEntityType, Collectors.toUnmodifiableSet()))
        .flatMap(edgeFieldsMap -> this.getEdgeSetRequestMap(context, edgeType, edgeFieldsMap));
  }

  private Single<Map<String, EdgeSetRequest>> getEdgeSetRequestMap(
      GraphQlRequestContext context,
      EdgeType edgeType,
      Map<String, Set<SelectedField>> entityTypeToEdgeFieldsMap) {
    return Observable.fromIterable(entityTypeToEdgeFieldsMap.entrySet())
        .flatMapSingle(
            entry ->
                this.getEdgeSetRequestEntry(context, edgeType, entry.getKey(), entry.getValue()))
        .collect(Collectors.toUnmodifiableMap(Entry::getKey, Entry::getValue));
  }

  private Single<Map.Entry<String, EdgeSetRequest>> getEdgeSetRequestEntry(
      GraphQlRequestContext context,
      EdgeType edgeType,
      String entityType,
      Set<SelectedField> edgeFields) {
    return zip(
        this.getRequestedAndRequiredAttributes(context, edgeFields, edgeType),
        this.getMetricAggregationRequestAttributes(context, edgeFields),
        this.getFilterArguments(context, edgeFields),
        (requestAttributes, metricAttributes, filters) ->
            Map.entry(
                entityType,
                new DefaultEdgeSetRequest(requestAttributes, metricAttributes, filters)));
  }

  private Map<String, Set<SelectedField>> getEdgesSelectionByType(
      Stream<SelectedField> edgeSetStream) {
    return edgeSetStream.collect(
        Collectors.groupingBy(
            this::getEntityType,
            Collectors.flatMapping(this::getEdgesForEdgeSet, Collectors.toUnmodifiableSet())));
  }

  private Stream<SelectedField> getEdgesForEdgeSet(SelectedField edgeSet) {
    return this.selectionFinder.findSelections(
        edgeSet.getSelectionSet(), SelectionQuery.namedChild(ResultSet.RESULT_SET_RESULTS_NAME));
  }

  private String getEntityType(SelectedField edgeSetField) {
    return this.argumentDeserializer
        .deserializePrimitive(edgeSetField.getArguments(), NeighborEntityTypeArgument.class)
        .map(EntityType::getScopeString)
        .or(
            () ->
                this.argumentDeserializer.deserializePrimitive(
                    edgeSetField.getArguments(), NeighborEntityScopeArgument.class))
        .orElseThrow();
  }

  private Single<List<MetricAggregationRequest>> getMetricAggregationRequestAttributes(
      GraphQlRequestContext context, Collection<SelectedField> edges) {
    Set<SelectedField> selections =
        edges.stream()
            .collect(
                Collectors.flatMapping(this::getEdgesForEdgeSet, Collectors.toUnmodifiableSet()));
    return this.metricAggregationRequestBuilder.build(
        context, HypertraceAttributeScopeString.INTERACTION, selections.stream());
  }

  private Single<List<AttributeRequest>> getRequestedAndRequiredAttributes(
      GraphQlRequestContext context, Collection<SelectedField> edges, EdgeType edgeType) {
    Set<SelectedField> selections =
        edges.stream()
            .collect(
                Collectors.flatMapping(this::getEdgesForEdgeSet, Collectors.toUnmodifiableSet()));
    return this.attributeRequestBuilder
        .buildForAttributeQueryableFields(
            context, HypertraceAttributeScopeString.INTERACTION, selections.stream())
        .mergeWith(this.getNeighborIdAttribute(context, edgeType))
        .mergeWith(this.getNeighborTypeAttribute(context, edgeType))
        .collect(Collectors.toUnmodifiableList());
  }

  private Single<AttributeRequest> getNeighborIdAttribute(
      GraphQlRequestContext context, EdgeType edgeType) {
    switch (edgeType) {
      case INCOMING:
        return this.attributeRequestBuilder.buildForAttributeExpression(
            context,
            HypertraceAttributeScopeString.INTERACTION,
            AttributeExpression.forAttributeKey(INCOMING_ENTITY_ID_KEY));
      case OUTGOING:
        return this.attributeRequestBuilder.buildForAttributeExpression(
            context,
            HypertraceAttributeScopeString.INTERACTION,
            AttributeExpression.forAttributeKey(OUTGOING_ENTITY_ID_KEY));
      default:
        return Single.error(new IllegalStateException("Unexpected value: " + edgeType));
    }
  }

  private Single<List<AttributeAssociation<FilterArgument>>> getFilterArguments(
      GraphQlRequestContext context, Set<SelectedField> edgeFields) {
    Set<FilterArgument> filterArguments =
        edgeFields.stream()
            .collect(Collectors.flatMapping(this::getFilter, Collectors.toUnmodifiableSet()));

    return this.filterRequestBuilder.build(
        context, HypertraceAttributeScopeString.INTERACTION, filterArguments);
  }

  private Stream<FilterArgument> getFilter(SelectedField selectedField) {
    return this.argumentDeserializer
        .deserializeObjectList(selectedField.getArguments(), FilterArgument.class)
        .stream()
        .flatMap(Collection::stream);
  }

  private Single<AttributeRequest> getNeighborTypeAttribute(
      GraphQlRequestContext context, EdgeType edgeType) {
    switch (edgeType) {
      case INCOMING:
        return this.attributeRequestBuilder.buildForAttributeExpression(
            context,
            HypertraceAttributeScopeString.INTERACTION,
            AttributeExpression.forAttributeKey(INCOMING_ENTITY_TYPE_KEY));
      case OUTGOING:
        return this.attributeRequestBuilder.buildForAttributeExpression(
            context,
            HypertraceAttributeScopeString.INTERACTION,
            AttributeExpression.forAttributeKey(OUTGOING_ENTITY_TYPE_KEY));
      default:
        return Single.error(new IllegalStateException("Unexpected value: " + edgeType));
    }
  }

  private enum EdgeType {
    INCOMING,
    OUTGOING
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultEdgeSetGroupRequest implements EdgeSetGroupRequest {
    AttributeRequest neighborIdAttribute;
    AttributeRequest neighborTypeAttribute;
    Map<String, EdgeSetRequest> edgeSetRequests;
    BiFunction<String, Collection<String>, Single<EntityRequest>> neighborRequestBuilder;

    @Override
    public Single<EntityRequest> buildNeighborRequest(
        String entityType, Collection<String> neighborIds) {
      return this.neighborRequestBuilder.apply(entityType, neighborIds);
    }

    @Override
    public Collection<AttributeRequest> getAllAttributeRequests() {
      return this.edgeSetRequests.values().stream()
          .map(EdgeSetRequest::attributeRequests)
          .flatMap(Collection::stream)
          .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Collection<MetricAggregationRequest> getAllMetricAggregationRequests() {
      return this.edgeSetRequests.values().stream()
          .map(EdgeSetRequest::metricAggregationRequests)
          .flatMap(Collection::stream)
          .collect(Collectors.toUnmodifiableSet());
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultEdgeSetRequest implements EdgeSetRequest {
    Collection<AttributeRequest> attributeRequests;
    Collection<MetricAggregationRequest> metricAggregationRequests;
    Collection<AttributeAssociation<FilterArgument>> filterArguments;
  }
}
