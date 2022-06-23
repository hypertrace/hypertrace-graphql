package org.hypertrace.graphql.spanprocessing.deserialization;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.RateLimit;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.RateLimitType;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.WindowedRateLimit;

@Value
@Accessors(fluent = true)
@Jacksonized
@Builder
public class DefaultRateLimit implements RateLimit {
  RateLimitType rateLimitType;
  WindowedRateLimit fixedWindowLimit;
}
