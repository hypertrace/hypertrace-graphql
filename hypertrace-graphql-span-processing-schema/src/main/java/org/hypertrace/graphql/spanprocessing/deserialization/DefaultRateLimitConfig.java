package org.hypertrace.graphql.spanprocessing.deserialization;

import java.time.Duration;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.RateLimit;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.RateLimitConfig;

@Value
@Accessors(fluent = true)
@Jacksonized
@Builder
public class DefaultRateLimitConfig implements RateLimitConfig {
  RateLimit traceLimitPerEndpoint;
  RateLimit traceLimitGlobal;
  Duration apiEndpointCacheDuration;
}
