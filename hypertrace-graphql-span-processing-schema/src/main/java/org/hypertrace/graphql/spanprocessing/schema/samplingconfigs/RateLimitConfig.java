package org.hypertrace.graphql.spanprocessing.schema.samplingconfigs;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.time.Duration;

@GraphQLName(RateLimitConfig.TYPE_NAME)
public interface RateLimitConfig {
  String TYPE_NAME = "SpanProcessingRateLimitConfig";

  String TRACE_LIMIT_PER_ENDPOINT_KEY = "traceLimitPerEndpoint";
  String TRACE_LIMIT_GLOBAL_KEY = "traceLimitGlobal";
  String API_ENDPOINT_CACHE_DURATION_KEY = "apiEndpointCacheDuration";

  @GraphQLField
  @GraphQLName(TRACE_LIMIT_PER_ENDPOINT_KEY)
  @GraphQLNonNull
  RateLimit traceLimitPerEndpoint();

  @GraphQLField
  @GraphQLName(TRACE_LIMIT_GLOBAL_KEY)
  @GraphQLNonNull
  RateLimit traceLimitGlobal();

  @GraphQLField
  @GraphQLName(API_ENDPOINT_CACHE_DURATION_KEY)
  @GraphQLNonNull
  Duration apiEndpointCacheDuration();
}
