package org.hypertrace.graphql.entity.request;

import static io.reactivex.rxjava3.core.Single.zip;
import static org.hypertrace.graphql.entity.schema.Edge.EDGE_NEIGHBOR_KEY;
import static org.hypertrace.graphql.entity.schema.Entity.ENTITY_INCOMING_EDGES_KEY;
import static org.hypertrace.graphql.entity.schema.Entity.ENTITY_OUTGOING_EDGES_KEY;

import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequestBuilder;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterOperatorType;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterType;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;
import org.hypertrace.graphql.entity.schema.EntityType;
import org.hypertrace.graphql.metric.request.MetricRequest;
import org.hypertrace.graphql.metric.request.MetricRequestBuilder;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;

class NeighborEntitiesRequestBuilder {

  private static final SelectionQuery INCOMING_EDGE_QUERY =
      SelectionQuery.namedChild(ENTITY_INCOMING_EDGES_KEY);
  private static final SelectionQuery OUTGOING_EDGE_QUERY =
      SelectionQuery.namedChild(ENTITY_OUTGOING_EDGES_KEY);
  private static final int NEIGHBOR_QUERY_OFFSET = 0;
  private final GraphQlSelectionFinder selectionFinder;
  private final ResultSetRequestBuilder resultSetRequestBuilder;
  private final MetricRequestBuilder metricRequestBuilder;
  private final Converter<EntityType, AttributeModelScope> scopeConverter;
  private final EdgeRequestBuilder edgeRequestBuilder;
  private final AttributeStore attributeStore;

  @Inject
  NeighborEntitiesRequestBuilder(
      GraphQlSelectionFinder selectionFinder,
      ResultSetRequestBuilder resultSetRequestBuilder,
      MetricRequestBuilder metricRequestBuilder,
      Converter<EntityType, AttributeModelScope> scopeConverter,
      EdgeRequestBuilder edgeRequestBuilder,
      AttributeStore attributeStore) {
    this.selectionFinder = selectionFinder;
    this.resultSetRequestBuilder = resultSetRequestBuilder;
    this.metricRequestBuilder = metricRequestBuilder;
    this.scopeConverter = scopeConverter;
    this.edgeRequestBuilder = edgeRequestBuilder;
    this.attributeStore = attributeStore;
  }

  Single<EntityRequest> buildNeighborRequest(
      GraphQlRequestContext context,
      EntityType entityType,
      TimeRangeArgument timeRange,
      Collection<String> neighborIds,
      Collection<SelectedField> edgeFields) {

    return this.scopeConverter
        .convert(entityType)
        .flatMap(
            entityScope ->
                this.build(
                    context,
                    entityType,
                    timeRange,
                    entityScope,
                    neighborIds,
                    this.getNeighborFields(edgeFields)));
  }

  private Single<EntityRequest> build(
      GraphQlRequestContext context,
      EntityType entityType,
      TimeRangeArgument timeRange,
      AttributeModelScope scope,
      Collection<String> neighborIds,
      Collection<SelectedField> neighborFields) {
    return zip(
        this.buildResultSetRequest(context, timeRange, scope, neighborIds, neighborFields),
        this.metricRequestBuilder.build(context, scope, neighborFields.stream()),
        this.edgeRequestBuilder.buildIncomingEdgeRequest(
            context, timeRange, this.getIncomingEdges(neighborFields)),
        this.edgeRequestBuilder.buildOutgoingEdgeRequest(
            context, timeRange, this.getOutgoingEdges(neighborFields)),
        (resultSetRequest, metricRequestList, incomingEdges, outgoingEdges) ->
            new NeighborEntityRequest(
                entityType, resultSetRequest, metricRequestList, incomingEdges, outgoingEdges));
  }

  private Single<ResultSetRequest<AggregatableOrderArgument>> buildResultSetRequest(
      GraphQlRequestContext context,
      TimeRangeArgument timeRange,
      AttributeModelScope scope,
      Collection<String> neighborIds,
      Collection<SelectedField> neighborFields) {
    return this.getIdFilter(context, scope, neighborIds)
        .flatMap(
            filters ->
                this.resultSetRequestBuilder.build(
                    context,
                    scope,
                    neighborIds.size(),
                    NEIGHBOR_QUERY_OFFSET,
                    timeRange,
                    Collections.emptyList(),
                    filters,
                    neighborFields.stream()));
  }

  private Single<Collection<AttributeAssociation<FilterArgument>>> getIdFilter(
      GraphQlRequestContext context,
      AttributeModelScope neighborScope,
      Collection<String> neighborIds) {
    return this.attributeStore
        .getIdAttribute(context, neighborScope)
        .map(
            idAttribute ->
                AttributeAssociation.<FilterArgument>of(
                    idAttribute, new EntityNeighborIdFilter(idAttribute.key(), neighborIds)))
        .map(Set::of);
  }

  private Stream<SelectedField> getIncomingEdges(Collection<SelectedField> neighborFields) {
    return neighborFields.stream()
        .map(SelectedField::getSelectionSet)
        .flatMap(
            neighborSelections ->
                this.selectionFinder.findSelections(neighborSelections, INCOMING_EDGE_QUERY));
  }

  private Stream<SelectedField> getOutgoingEdges(Collection<SelectedField> neighborFields) {
    return neighborFields.stream()
        .map(SelectedField::getSelectionSet)
        .flatMap(
            neighborSelections ->
                this.selectionFinder.findSelections(neighborSelections, OUTGOING_EDGE_QUERY));
  }

  private Collection<SelectedField> getNeighborFields(Collection<SelectedField> edgeFields) {
    return edgeFields.stream()
        .flatMap(this::getNeighborFields)
        .collect(Collectors.toUnmodifiableSet());
  }

  private Stream<SelectedField> getNeighborFields(SelectedField edgeField) {
    return this.selectionFinder.findSelections(
        edgeField.getSelectionSet(), SelectionQuery.namedChild(EDGE_NEIGHBOR_KEY));
  }

  @Value
  @Accessors(fluent = true)
  private static class EntityNeighborIdFilter implements FilterArgument {
    FilterType type = FilterType.ATTRIBUTE;
    String key;
    FilterOperatorType operator = FilterOperatorType.IN;
    Collection<String> value;
    AttributeScope idScope = null; // Easier to use a plain attribute filter rather than convert
  }

  @Value
  @Accessors(fluent = true)
  private static class NeighborEntityRequest implements EntityRequest {
    EntityType entityType;
    ResultSetRequest<AggregatableOrderArgument> resultSetRequest;
    List<MetricRequest> metricRequests;
    EdgeSetGroupRequest incomingEdgeRequests;
    EdgeSetGroupRequest outgoingEdgeRequests;
  }
}
