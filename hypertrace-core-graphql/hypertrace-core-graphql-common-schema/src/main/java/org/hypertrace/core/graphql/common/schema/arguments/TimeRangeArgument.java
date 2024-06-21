package org.hypertrace.core.graphql.common.schema.arguments;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.time.Instant;

@GraphQLName(TimeRangeArgument.TYPE_NAME)
public interface TimeRangeArgument {
  String TYPE_NAME = "TimeRange";
  String ARGUMENT_NAME = "between"; // TODO rename to time range
  String TIME_RANGE_ARGUMENT_START_TIME = "startTime";
  String TIME_RANGE_ARGUMENT_END_TIME = "endTime";

  @GraphQLField
  @GraphQLName(TIME_RANGE_ARGUMENT_START_TIME)
  @GraphQLNonNull
  Instant startTime();

  @GraphQLField
  @GraphQLName(TIME_RANGE_ARGUMENT_END_TIME)
  @GraphQLNonNull
  Instant endTime();
}
