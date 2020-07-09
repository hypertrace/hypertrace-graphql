package org.hypertrace.graphql.entity.schema;

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
import org.hypertrace.graphql.entity.fetcher.EntityFetcher;
import org.hypertrace.graphql.entity.schema.argument.EntityTypeArgument;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;

public interface EntitySchema {
  String ENTITIES_QUERY_NAME = "entities";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ENTITIES_QUERY_NAME)
  @GraphQLDataFetcher(EntityFetcher.class)
  EntityResultSet entities(
      @GraphQLName(EntityTypeArgument.ARGUMENT_NAME) @GraphQLNonNull EntityType entityType,
      @GraphQLName(TimeRangeArgument.ARGUMENT_NAME) @GraphQLNonNull TimeRangeArgument timeRange,
      @GraphQLName(FilterArgument.ARGUMENT_NAME) List<FilterArgument> filterBy,
      @GraphQLName(OrderArgument.ARGUMENT_NAME) List<AggregatableOrderArgument> orderBy,
      @GraphQLName(LimitArgument.ARGUMENT_NAME) int limit,
      @GraphQLName(OffsetArgument.ARGUMENT_NAME) int offset);
}
