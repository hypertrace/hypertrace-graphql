package org.hypertrace.graphql.spanprocessing.deserialization;

import java.time.Duration;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.WindowedRateLimit;

@Value
@Accessors(fluent = true)
@Jacksonized
@Builder
public class DefaultWindowedRateLimit implements WindowedRateLimit {
  Long quantityAllowed;
  Duration windowDuration;
}
