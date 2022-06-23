package org.hypertrace.graphql.spanprocessing.dao;

import static org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.RateLimitType.WINDOWED;

import java.time.Duration;
import java.util.NoSuchElementException;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.RateLimit;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.RateLimitType;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.WindowedRateLimit;

public class ConfigServiceRateLimitConverter {

  public RateLimit convert(org.hypertrace.span.processing.config.service.v1.RateLimit rateLimit) {
    switch (rateLimit.getLimitCase()) {
      case FIXED_WINDOW_LIMIT:
        return ConvertedRateLimit.builder()
            .rateLimitType(WINDOWED)
            .fixedWindowLimit(convertWindowedRateLimit(rateLimit.getFixedWindowLimit()))
            .build();
      default:
        throw new NoSuchElementException("Unsupported Rate limit type: " + rateLimit);
    }
  }

  public org.hypertrace.span.processing.config.service.v1.RateLimit convert(RateLimit rateLimit) {
    switch (rateLimit.rateLimitType()) {
      case WINDOWED:
        Duration windowDuration = rateLimit.fixedWindowLimit().windowDuration();
        return org.hypertrace.span.processing.config.service.v1.RateLimit.newBuilder()
            .setFixedWindowLimit(
                org.hypertrace.span.processing.config.service.v1.WindowedRateLimit.newBuilder()
                    .setQuantityAllowed(rateLimit.fixedWindowLimit().quantityAllowed())
                    .setWindowDuration(
                        com.google.protobuf.Duration.newBuilder()
                            .setSeconds(windowDuration.getSeconds())
                            .setNanos(windowDuration.getNano())
                            .build())
                    .build())
            .build();
      default:
        throw new NoSuchElementException("Unsupported Rate limit type: " + rateLimit);
    }
  }

  private WindowedRateLimit convertWindowedRateLimit(
      org.hypertrace.span.processing.config.service.v1.WindowedRateLimit windowedRateLimit) {
    return ConvertedWindowedRateLimit.builder()
        .quantityAllowed(windowedRateLimit.getQuantityAllowed())
        .windowDuration(
            Duration.ofSeconds(
                windowedRateLimit.getWindowDuration().getSeconds(),
                windowedRateLimit.getWindowDuration().getNanos()))
        .build();
  }

  @Value
  @Builder
  @Accessors(fluent = true)
  private static class ConvertedRateLimit implements RateLimit {
    RateLimitType rateLimitType;
    WindowedRateLimit fixedWindowLimit;
  }

  @Value
  @Builder
  @Accessors(fluent = true)
  private static class ConvertedWindowedRateLimit implements WindowedRateLimit {
    Long quantityAllowed;
    Duration windowDuration;
  }
}
