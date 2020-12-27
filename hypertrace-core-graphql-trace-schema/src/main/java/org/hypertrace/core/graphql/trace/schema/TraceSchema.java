package org.hypertrace.core.graphql.trace.schema;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.LimitArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.OffsetArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.space.SpaceArgument;
import org.hypertrace.core.graphql.trace.fetcher.TraceFetcher;
import org.hypertrace.core.graphql.trace.schema.arguments.TraceType;
import org.hypertrace.core.graphql.trace.schema.arguments.TraceTypeArgument;

public interface TraceSchema {
  String TRACE_QUERY_NAME = "traces";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(TRACE_QUERY_NAME)
  @GraphQLDataFetcher(TraceFetcher.class)
  TraceResultSet traces(
      @Deprecated @GraphQLName(TraceTypeArgument.ARGUMENT_NAME) TraceType type,
      @GraphQLName(TimeRangeArgument.ARGUMENT_NAME) @GraphQLNonNull TimeRangeArgument between,
      @GraphQLName(FilterArgument.ARGUMENT_NAME) List<FilterArgument> filterBy,
      @GraphQLName(OrderArgument.ARGUMENT_NAME) List<OrderArgument> orderBy,
      @GraphQLName(LimitArgument.ARGUMENT_NAME) int limit,
      @GraphQLName(OffsetArgument.ARGUMENT_NAME) int offset,
      @GraphQLName(SpaceArgument.ARGUMENT_NAME) String space);
}
