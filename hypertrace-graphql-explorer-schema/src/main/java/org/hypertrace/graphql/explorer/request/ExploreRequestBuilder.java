package org.hypertrace.graphql.explorer.request;

import graphql.schema.DataFetchingFieldSelectionSet;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.explorer.schema.argument.EntityContextOptions;
import org.hypertrace.graphql.explorer.schema.argument.GroupByArgument;
import org.hypertrace.graphql.explorer.schema.argument.IntervalArgument;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;

public interface ExploreRequestBuilder {
  Single<ExploreRequest> build(
      GraphQlRequestContext context,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet);

  Single<ExploreRequest> build(
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
      Single<Set<MetricAggregationRequest>> aggregationSelections);
}
