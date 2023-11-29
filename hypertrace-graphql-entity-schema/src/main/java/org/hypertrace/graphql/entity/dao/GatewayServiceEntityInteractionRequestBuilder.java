package org.hypertrace.graphql.entity.dao;

import static io.reactivex.rxjava3.core.Single.zip;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterOperatorType;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterType;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.Filter;
import org.hypertrace.gateway.service.v1.common.Operator;
import org.hypertrace.gateway.service.v1.entity.InteractionsRequest;
import org.hypertrace.graphql.entity.request.EdgeSetGroupRequest;
import org.hypertrace.graphql.entity.request.EdgeSetRequest;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;

class GatewayServiceEntityInteractionRequestBuilder {

  private static final Integer DEFAULT_INTERACTION_LIMIT = 1000;
  private final Converter<Collection<AttributeRequest>, Set<Expression>> selectionConverter;
  private final Converter<Collection<MetricAggregationRequest>, Set<Expression>>
      aggregationConverter;
  private final Converter<Collection<AttributeAssociation<FilterArgument>>, Filter> filterConverter;

  @Inject
  GatewayServiceEntityInteractionRequestBuilder(
      Converter<Collection<AttributeRequest>, Set<Expression>> selectionConverter,
      Converter<Collection<MetricAggregationRequest>, Set<Expression>> aggregationConverter,
      Converter<Collection<AttributeAssociation<FilterArgument>>, Filter> filterConverter) {
    this.selectionConverter = selectionConverter;
    this.aggregationConverter = aggregationConverter;
    this.filterConverter = filterConverter;
  }

  Single<InteractionsRequest> build(EdgeSetGroupRequest edgeSetRequestGroup) {
    if (edgeSetRequestGroup.edgeSetRequests().isEmpty()) {
      return Single.just(InteractionsRequest.getDefaultInstance());
    }

    return zip(
        this.collectSelectionsAndAggregations(edgeSetRequestGroup),
        this.buildEntityInteractionFilter(edgeSetRequestGroup),
        (selections, filter) ->
            InteractionsRequest.newBuilder()
                .addAllSelection(selections)
                .setFilter(filter)
                .setLimit(DEFAULT_INTERACTION_LIMIT)
                .build());
  }

  private Single<Set<Expression>> collectSelectionsAndAggregations(EdgeSetGroupRequest request) {
    return this.selectionConverter
        .convert(getAllAttributeRequests(request))
        .mergeWith(this.aggregationConverter.convert(getAllMetricAggregationRequests(request)))
        .toObservable()
        .flatMap(Observable::fromIterable)
        .collect(Collectors.toUnmodifiableSet());
  }

  private Set<AttributeRequest> getAllAttributeRequests(EdgeSetGroupRequest request) {
    return request.edgeSetRequests().values().stream()
        .map(EdgeSetRequest::attributeRequests)
        .flatMap(Collection::stream)
        .collect(Collectors.toUnmodifiableSet());
  }

  private Set<MetricAggregationRequest> getAllMetricAggregationRequests(
      EdgeSetGroupRequest request) {
    return request.edgeSetRequests().values().stream()
        .map(EdgeSetRequest::metricAggregationRequests)
        .flatMap(Collection::stream)
        .collect(Collectors.toUnmodifiableSet());
  }

  private Single<Filter> buildEntityInteractionFilter(EdgeSetGroupRequest request) {
    // Todo: we should be using converter taking argument as logical filters with filter arg schema
    return Observable.fromIterable(request.edgeSetRequests().entrySet())
        .map(
            entry ->
                Stream.concat(
                        Stream.of(buildEntityTypeFilter(request, entry.getKey())),
                        entry.getValue().filterArguments().stream())
                    .collect(Collectors.toUnmodifiableList()))
        .flatMapSingle(this.filterConverter::convert)
        .collect(Collectors.toUnmodifiableList())
        .map(
            childFilters ->
                Filter.newBuilder()
                    .setOperator(Operator.OR)
                    .addAllChildFilter(childFilters)
                    .build());
  }

  private AttributeAssociation<FilterArgument> buildEntityTypeFilter(
      EdgeSetGroupRequest request, String entityType) {
    return AttributeAssociation.of(
        request.neighborTypeAttribute().attributeExpressionAssociation().attribute(),
        new EntityNeighborTypeFilter(
            request.neighborTypeAttribute().attributeExpressionAssociation().value(), entityType));
  }

  @Value
  @Accessors(fluent = true)
  private static class EntityNeighborTypeFilter implements FilterArgument {

    FilterType type = FilterType.ATTRIBUTE;
    String key = null;
    AttributeExpression keyExpression;
    FilterOperatorType operator = FilterOperatorType.EQUALS;
    String value;
    AttributeScope idType = null;
    String idScope = null;
  }
}
