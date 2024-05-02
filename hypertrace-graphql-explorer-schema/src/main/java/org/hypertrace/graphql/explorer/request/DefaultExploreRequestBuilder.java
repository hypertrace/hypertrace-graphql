package org.hypertrace.graphql.explorer.request;

import static io.reactivex.rxjava3.core.Single.zip;
import static java.util.Collections.emptyList;

import graphql.schema.DataFetchingFieldSelectionSet;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.request.FilterRequestBuilder;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.LimitArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.OffsetArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.space.SpaceArgument;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeScopeStringTranslator;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.explorer.schema.argument.EntityContextOptions;
import org.hypertrace.graphql.explorer.schema.argument.ExplorerContext;
import org.hypertrace.graphql.explorer.schema.argument.ExplorerContextArgument;
import org.hypertrace.graphql.explorer.schema.argument.ExplorerScopeArgument;
import org.hypertrace.graphql.explorer.schema.argument.GroupByArgument;
import org.hypertrace.graphql.explorer.schema.argument.IntervalArgument;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;

public class DefaultExploreRequestBuilder implements ExploreRequestBuilder {
  private static final int DEFAULT_LIMIT = 100;
  private static final int DEFAULT_OFFSET = 0;

  private final AttributeRequestBuilder attributeRequestBuilder;
  private final ArgumentDeserializer argumentDeserializer;
  private final ExploreSelectionRequestBuilder selectionRequestBuilder;
  private final FilterRequestBuilder filterRequestBuilder;
  private final AttributeScopeStringTranslator scopeStringTranslator;
  private final ExploreOrderArgumentBuilder exploreOrderArgumentBuilder;

  @Inject
  DefaultExploreRequestBuilder(
      AttributeRequestBuilder attributeRequestBuilder,
      ArgumentDeserializer argumentDeserializer,
      ExploreSelectionRequestBuilder selectionRequestBuilder,
      FilterRequestBuilder filterRequestBuilder,
      AttributeScopeStringTranslator scopeStringTranslator,
      ExploreOrderArgumentBuilder exploreOrderArgumentBuilder) {
    this.attributeRequestBuilder = attributeRequestBuilder;
    this.argumentDeserializer = argumentDeserializer;
    this.selectionRequestBuilder = selectionRequestBuilder;
    this.filterRequestBuilder = filterRequestBuilder;
    this.scopeStringTranslator = scopeStringTranslator;
    this.exploreOrderArgumentBuilder = exploreOrderArgumentBuilder;
  }

  @Override
  public Single<ExploreRequest> build(
      GraphQlRequestContext requestContext,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet) {

    String explorerScope =
        this.argumentDeserializer
            .deserializePrimitive(arguments, ExplorerContextArgument.class)
            .map(ExplorerContext::getScopeString)
            .or(
                () ->
                    this.argumentDeserializer.deserializePrimitive(
                        arguments, ExplorerScopeArgument.class))
            .map(this.scopeStringTranslator::fromExternal)
            .orElseThrow();

    int limit =
        this.argumentDeserializer
            .deserializePrimitive(arguments, LimitArgument.class)
            .orElse(DEFAULT_LIMIT);

    int offset =
        this.argumentDeserializer
            .deserializePrimitive(arguments, OffsetArgument.class)
            .orElse(DEFAULT_OFFSET);

    Optional<TimeRangeArgument> timeRange =
        this.argumentDeserializer.deserializeObject(arguments, TimeRangeArgument.class);

    List<AggregatableOrderArgument> requestedOrders =
        this.argumentDeserializer
            .deserializeObjectList(arguments, AggregatableOrderArgument.class)
            .orElse(emptyList());

    List<FilterArgument> requestedFilters =
        this.argumentDeserializer
            .deserializeObjectList(arguments, FilterArgument.class)
            .orElse(emptyList());

    Optional<GroupByArgument> groupBy =
        this.argumentDeserializer.deserializeObject(arguments, GroupByArgument.class);

    Optional<String> spaceId =
        this.argumentDeserializer.deserializePrimitive(arguments, SpaceArgument.class);

    Optional<IntervalArgument> intervalArgument =
        this.argumentDeserializer.deserializeObject(arguments, IntervalArgument.class);

    Optional<EntityContextOptions> entityContextOptions =
        this.argumentDeserializer.deserializeObject(arguments, EntityContextOptions.class);

    Single<Set<AttributeRequest>> attributeSelections =
        this.selectionRequestBuilder.getAttributeSelections(
            requestContext, explorerScope, selectionSet);

    Single<Set<MetricAggregationRequest>> aggregationSelections =
        this.selectionRequestBuilder.getAggregationSelections(
            requestContext, explorerScope, selectionSet);

    return build(
        requestContext,
        explorerScope,
        timeRange,
        spaceId,
        limit,
        offset,
        requestedFilters,
        requestedOrders,
        groupBy,
        intervalArgument,
        entityContextOptions,
        attributeSelections,
        aggregationSelections);
  }

  @Override
  public Single<ExploreRequest> build(
      GraphQlRequestContext requestContext,
      String explorerScope,
      Optional<TimeRangeArgument> timeRange,
      Optional<String> spaceId,
      int limit,
      int offset,
      List<FilterArgument> requestedFilters,
      List<AggregatableOrderArgument> requestedOrders,
      Optional<GroupByArgument> groupBy,
      Optional<IntervalArgument> intervalArgument,
      Optional<EntityContextOptions> entityContextOptions,
      Single<Set<AttributeRequest>> attributeSelections,
      Single<Set<MetricAggregationRequest>> aggregationSelections) {

    Set<AttributeExpression> groupByExpressions =
        groupBy.map(this::resolveGroupByExpressions).orElseGet(Collections::emptySet);

    Single<List<ExploreOrderArgument>> orderArguments =
        this.exploreOrderArgumentBuilder.buildList(requestContext, explorerScope, requestedOrders);

    Single<List<AttributeAssociation<FilterArgument>>> filterSingle =
        this.filterRequestBuilder.build(requestContext, explorerScope, requestedFilters);

    return zip(
        attributeSelections,
        aggregationSelections,
        orderArguments,
        filterSingle,
        this.buildGroupByAttributes(requestContext, explorerScope, groupByExpressions),
        (attributes, aggregations, orders, filters, groupByAttribute) ->
            new DefaultExploreRequest(
                requestContext,
                explorerScope,
                timeRange,
                limit > 0 ? limit : DEFAULT_LIMIT,
                offset >= 0 ? offset : DEFAULT_OFFSET,
                attributes,
                aggregations,
                orders,
                filters,
                groupByAttribute,
                intervalArgument,
                entityContextOptions,
                groupBy.map(GroupByArgument::includeRest).orElse(false),
                spaceId,
                groupBy.map(GroupByArgument::groupLimit)));
  }

  @Override
  public Single<ExploreRequest> rebuildWithAdditionalFilters(
      ExploreRequest originalRequest, List<FilterArgument> additionalFilterArguments) {
    return this.mergeFilterLists(
            originalRequest.context(),
            originalRequest.scope(),
            originalRequest.filterArguments(),
            additionalFilterArguments)
        .map(
            newFilterArguments ->
                new DefaultExploreRequest(
                    originalRequest.context(),
                    originalRequest.scope(),
                    originalRequest.timeRange(),
                    originalRequest.limit(),
                    originalRequest.offset(),
                    originalRequest.attributeRequests(),
                    originalRequest.aggregationRequests(),
                    originalRequest.orderArguments(),
                    newFilterArguments,
                    originalRequest.groupByAttributeRequests(),
                    originalRequest.timeInterval(),
                    originalRequest.entityContextOptions(),
                    originalRequest.includeRest(),
                    originalRequest.spaceId(),
                    originalRequest.groupLimit()));
  }

  private Single<List<AttributeAssociation<FilterArgument>>> mergeFilterLists(
      GraphQlRequestContext requestContext,
      String scope,
      Collection<AttributeAssociation<FilterArgument>> original,
      Collection<FilterArgument> additional) {
    return this.filterRequestBuilder
        .build(requestContext, scope, additional)
        .flattenAsObservable(list -> list)
        .concatWith(Observable.fromIterable(original))
        .toList();
  }

  private Single<Set<AttributeRequest>> buildGroupByAttributes(
      GraphQlRequestContext context,
      String explorerScope,
      Set<AttributeExpression> groupByExpressions) {
    return Observable.fromIterable(groupByExpressions)
        .flatMapSingle(
            expression ->
                this.attributeRequestBuilder.buildForAttributeExpression(
                    context, explorerScope, expression))
        .collect(Collectors.toUnmodifiableSet());
  }

  private Set<AttributeExpression> resolveGroupByExpressions(GroupByArgument groupByArgument) {
    return Optional.ofNullable(groupByArgument.expressions())
        .map(Set::copyOf)
        .orElseGet(
            () ->
                Optional.ofNullable(groupByArgument.keys())
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(AttributeExpression::forAttributeKey)
                    .collect(Collectors.toUnmodifiableSet()));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultExploreRequest implements ExploreRequest {
    GraphQlRequestContext context;
    String scope;
    Optional<TimeRangeArgument> timeRange;
    int limit;
    int offset;
    Set<AttributeRequest> attributeRequests;
    Set<MetricAggregationRequest> aggregationRequests;
    List<ExploreOrderArgument> orderArguments;
    List<AttributeAssociation<FilterArgument>> filterArguments;
    Set<AttributeRequest> groupByAttributeRequests;
    Optional<IntervalArgument> timeInterval;
    Optional<EntityContextOptions> entityContextOptions;
    boolean includeRest;
    Optional<String> spaceId;
    Optional<Integer> groupLimit;
  }
}
