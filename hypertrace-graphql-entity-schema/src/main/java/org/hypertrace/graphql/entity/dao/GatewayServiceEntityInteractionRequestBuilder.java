package org.hypertrace.graphql.entity.dao;

import static io.reactivex.rxjava3.core.Single.zip;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
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
import org.hypertrace.gateway.service.v1.entity.InteractionsRequest;
import org.hypertrace.graphql.entity.request.EdgeSetGroupRequest;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;

class GatewayServiceEntityInteractionRequestBuilder {

  private static final Integer DEFAULT_INTERACTION_LIMIT = 1000;
  private final Converter<Collection<AttributeRequest>, Set<Expression>> selectionConverter;
  private final Converter<Collection<MetricAggregationRequest>, Set<Expression>>
      aggregationConverter;
  private final Converter<Collection<Collection<AttributeAssociation<FilterArgument>>>, Filter>
      filterConverter;

  @Inject
  GatewayServiceEntityInteractionRequestBuilder(
      Converter<Collection<AttributeRequest>, Set<Expression>> selectionConverter,
      Converter<Collection<MetricAggregationRequest>, Set<Expression>> aggregationConverter,
      Converter<Collection<Collection<AttributeAssociation<FilterArgument>>>, Filter>
          filterConverter) {
    this.selectionConverter = selectionConverter;
    this.aggregationConverter = aggregationConverter;
    this.filterConverter = filterConverter;
  }

  Single<InteractionsRequest> build(EdgeSetGroupRequest edgeSetRequestGroup) {
    if (edgeSetRequestGroup.entityTypes().isEmpty()) {
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
        .convert(request.attributeRequests())
        .mergeWith(this.aggregationConverter.convert(request.metricAggregationRequests()))
        .toObservable()
        .flatMap(Observable::fromIterable)
        .collect(Collectors.toUnmodifiableSet());
  }

  private Single<Filter> buildEntityInteractionFilter(EdgeSetGroupRequest request) {
    return this.filterConverter.convert(this.buildFilterArguments(request));
  }

  private Collection<Collection<AttributeAssociation<FilterArgument>>> buildFilterArguments(
      EdgeSetGroupRequest request) {
    return request.filterArguments().entrySet().stream()
        .map(
            entry ->
                Stream.concat(
                        Stream.of(buildEntityTypeFilter(request, entry.getKey())),
                        entry.getValue().stream())
                    .collect(Collectors.toUnmodifiableList()))
        .collect(Collectors.toUnmodifiableList());
  }

  private AttributeAssociation<FilterArgument> buildEntityTypeFilter(
      EdgeSetGroupRequest request, String entityType) {
    return AttributeAssociation.of(
        request.neighborTypeAttribute().attributeExpressionAssociation().attribute(),
        new EntityNeighborTypeFilter(
            request.neighborTypeAttribute().attributeExpressionAssociation().value(),
            List.of(entityType)));
  }

  @Value
  @Accessors(fluent = true)
  private static class EntityNeighborTypeFilter implements FilterArgument {
    FilterType type = FilterType.ATTRIBUTE;
    String key = null;
    AttributeExpression keyExpression;
    FilterOperatorType operator = FilterOperatorType.IN;
    Collection<String> value;
    AttributeScope idType = null;
    String idScope = null;
  }
}
