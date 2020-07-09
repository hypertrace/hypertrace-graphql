package org.hypertrace.graphql.entity.request;

import static io.reactivex.rxjava3.core.Single.zip;

import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Provider;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;
import org.hypertrace.graphql.entity.schema.EntityType;
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
  private final AttributeRequestBuilder attributeRequestBuilder;
  // Use provider to avoid cycle
  private final Provider<NeighborEntitiesRequestBuilder> neighborEntitiesRequestBuilderProvider;

  @Inject
  EdgeRequestBuilder(
      ArgumentDeserializer argumentDeserializer,
      GraphQlSelectionFinder selectionFinder,
      MetricAggregationRequestBuilder metricAggregationRequestBuilder,
      AttributeRequestBuilder attributeRequestBuilder,
      Provider<NeighborEntitiesRequestBuilder> neighborEntitiesRequestBuilderProvider) {
    this.argumentDeserializer = argumentDeserializer;
    this.selectionFinder = selectionFinder;
    this.metricAggregationRequestBuilder = metricAggregationRequestBuilder;
    this.attributeRequestBuilder = attributeRequestBuilder;
    this.neighborEntitiesRequestBuilderProvider = neighborEntitiesRequestBuilderProvider;
  }

  Single<EdgeSetGroupRequest> buildIncomingEdgeRequest(
      GraphQlRequestContext context,
      TimeRangeArgument timeRange,
      Stream<SelectedField> edgeSetFields) {
    return this.buildEdgeRequest(
        context, timeRange, this.getEdgesByType(edgeSetFields), EdgeType.INCOMING);
  }

  Single<EdgeSetGroupRequest> buildOutgoingEdgeRequest(
      GraphQlRequestContext context,
      TimeRangeArgument timeRange,
      Stream<SelectedField> edgeSetFields) {
    return this.buildEdgeRequest(
        context, timeRange, this.getEdgesByType(edgeSetFields), EdgeType.OUTGOING);
  }

  private Single<EdgeSetGroupRequest> buildEdgeRequest(
      GraphQlRequestContext context,
      TimeRangeArgument timeRange,
      Map<EntityType, Set<SelectedField>> edgesByType,
      EdgeType edgeType) {

    Set<SelectedField> allEdges =
        edgesByType.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableSet());

    return zip(
        this.getRequestedAndRequiredAttributes(context, allEdges, edgeType),
        this.getNeighborIdAttribute(context, edgeType),
        this.getNeighborTypeAttribute(context, edgeType),
        this.metricAggregationRequestBuilder.build(
            context, AttributeModelScope.INTERACTION, allEdges.stream()),
        (attributeRequests, neighborIdRequest, neighborTypeRequest, metricRequests) ->
            new DefaultEdgeSetGroupRequest(
                edgesByType.keySet(),
                attributeRequests,
                metricRequests,
                neighborIdRequest,
                neighborTypeRequest,
                (entityType, neighborIds) ->
                    this.neighborEntitiesRequestBuilderProvider
                        .get()
                        .buildNeighborRequest(
                            context,
                            entityType,
                            timeRange,
                            neighborIds,
                            edgesByType.get(entityType))));
  }

  private Map<EntityType, Set<SelectedField>> getEdgesByType(Stream<SelectedField> edgeSetStream) {

    return edgeSetStream.collect(
        Collectors.groupingBy(
            this::getEntityType,
            Collectors.flatMapping(this::getEdgesForEdgeSet, Collectors.toUnmodifiableSet())));
  }

  private Stream<SelectedField> getEdgesForEdgeSet(SelectedField edgeSet) {
    return this.selectionFinder.findSelections(
        edgeSet.getSelectionSet(), SelectionQuery.namedChild(ResultSet.RESULT_SET_RESULTS_NAME));
  }

  private EntityType getEntityType(SelectedField edgeSetField) {
    return this.argumentDeserializer
        .deserializePrimitive(edgeSetField.getArguments(), NeighborEntityTypeArgument.class)
        .orElseThrow();
  }

  private Single<List<AttributeRequest>> getRequestedAndRequiredAttributes(
      GraphQlRequestContext context, Collection<SelectedField> edges, EdgeType edgeType) {
    return this.attributeRequestBuilder
        .buildForAttributeQueryableFields(context, AttributeModelScope.INTERACTION, edges.stream())
        .mergeWith(this.getNeighborIdAttribute(context, edgeType))
        .mergeWith(this.getNeighborTypeAttribute(context, edgeType))
        .collect(Collectors.toUnmodifiableList());
  }

  private Single<AttributeRequest> getNeighborIdAttribute(
      GraphQlRequestContext context, EdgeType edgeType) {
    switch (edgeType) {
      case INCOMING:
        return this.attributeRequestBuilder.buildForKey(
            context, AttributeModelScope.INTERACTION, INCOMING_ENTITY_ID_KEY);
      case OUTGOING:
        return this.attributeRequestBuilder.buildForKey(
            context, AttributeModelScope.INTERACTION, OUTGOING_ENTITY_ID_KEY);
      default:
        return Single.error(new IllegalStateException("Unexpected value: " + edgeType));
    }
  }

  private Single<AttributeRequest> getNeighborTypeAttribute(
      GraphQlRequestContext context, EdgeType edgeType) {
    switch (edgeType) {
      case INCOMING:
        return this.attributeRequestBuilder.buildForKey(
            context, AttributeModelScope.INTERACTION, INCOMING_ENTITY_TYPE_KEY);
      case OUTGOING:
        return this.attributeRequestBuilder.buildForKey(
            context, AttributeModelScope.INTERACTION, OUTGOING_ENTITY_TYPE_KEY);
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
    Set<EntityType> entityTypes;
    Collection<AttributeRequest> attributeRequests;
    Collection<MetricAggregationRequest> metricAggregationRequests;
    AttributeRequest neighborIdAttribute;
    AttributeRequest neighborTypeAttribute;
    BiFunction<EntityType, Collection<String>, Single<EntityRequest>> neighborRequestBuilder;

    @Override
    public Single<EntityRequest> buildNeighborRequest(
        EntityType entityType, Collection<String> neighborIds) {
      return this.neighborRequestBuilder.apply(entityType, neighborIds);
    }
  }
}
