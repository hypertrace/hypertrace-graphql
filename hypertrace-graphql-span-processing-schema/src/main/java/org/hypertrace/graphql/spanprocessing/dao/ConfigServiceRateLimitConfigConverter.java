package org.hypertrace.graphql.spanprocessing.dao;

import io.reactivex.rxjava3.core.Single;
import java.time.Duration;
import javax.inject.Inject;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.RateLimit;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.RateLimitConfig;

public class ConfigServiceRateLimitConfigConverter {

  private final ConfigServiceRateLimitConverter rateLimitConverter;

  @Inject
  ConfigServiceRateLimitConfigConverter(ConfigServiceRateLimitConverter rateLimitConverter) {
    this.rateLimitConverter = rateLimitConverter;
  }

  public Single<RateLimitConfig> convert(
      org.hypertrace.span.processing.config.service.v1.RateLimitConfig rateLimitConfig) {
    return Single.just(
        ConvertedRateLimitConfig.builder()
            .traceLimitGlobal(rateLimitConverter.convert(rateLimitConfig.getTraceLimitGlobal()))
            .traceLimitPerEndpoint(
                rateLimitConverter.convert(rateLimitConfig.getTraceLimitPerEndpoint()))
            .apiEndpointCacheDuration(
                Duration.ofSeconds(
                    rateLimitConfig.getApiEndpointCacheDuration().getSeconds(),
                    rateLimitConfig.getApiEndpointCacheDuration().getNanos()))
            .build());
  }

  public org.hypertrace.span.processing.config.service.v1.RateLimitConfig convert(
      RateLimitConfig rateLimitConfig) {
    return org.hypertrace.span.processing.config.service.v1.RateLimitConfig.newBuilder()
        .setTraceLimitGlobal(rateLimitConverter.convert(rateLimitConfig.traceLimitGlobal()))
        .setTraceLimitPerEndpoint(
            rateLimitConverter.convert(rateLimitConfig.traceLimitPerEndpoint()))
        .setApiEndpointCacheDuration(
            com.google.protobuf.Duration.newBuilder()
                .setSeconds(rateLimitConfig.apiEndpointCacheDuration().getSeconds())
                .setNanos(rateLimitConfig.apiEndpointCacheDuration().getNano())
                .build())
        .build();
  }

  @Value
  @Builder
  @Accessors(fluent = true)
  private static class ConvertedRateLimitConfig implements RateLimitConfig {
    RateLimit traceLimitPerEndpoint;
    RateLimit traceLimitGlobal;
    Duration apiEndpointCacheDuration;
  }
}
