package org.hypertrace.graphql.spanprocessing.schema.samplingconfigs;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(RateLimit.TYPE_NAME)
public interface RateLimit {
  String TYPE_NAME = "SpanProcessingRateLimit";

  String RATE_LIMIT_TYPE_KEY = "rateLimitType";
  String FIXED_WINDOW_LIMIT_KEY = "fixedWindowLimit";

  @GraphQLField
  @GraphQLName(RATE_LIMIT_TYPE_KEY)
  @GraphQLNonNull
  RateLimitType rateLimitType();

  @GraphQLField
  @GraphQLName(FIXED_WINDOW_LIMIT_KEY)
  WindowedRateLimit fixedWindowLimit();
}
