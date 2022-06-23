package org.hypertrace.graphql.spanprocessing.dao;

import static io.reactivex.rxjava3.core.Single.zip;

import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import javax.inject.Inject;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.RateLimitConfig;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.SamplingConfig;
import org.hypertrace.span.processing.config.service.v1.SamplingConfigInfo;
import org.hypertrace.span.processing.config.service.v1.SamplingConfigMetadata;

public class ConfigServiceSamplingConfigConverter {

  private final ConfigServiceSpanFilterConverter filterConverter;
  private final ConfigServiceRateLimitConfigConverter rateLimitConfigConverter;

  @Inject
  ConfigServiceSamplingConfigConverter(
      ConfigServiceSpanFilterConverter filterConverter,
      ConfigServiceRateLimitConfigConverter rateLimitConfigConverter) {
    this.filterConverter = filterConverter;
    this.rateLimitConfigConverter = rateLimitConfigConverter;
  }

  public Single<SamplingConfig> convert(
      org.hypertrace.span.processing.config.service.v1.SamplingConfigDetails
          samplingConfigDetails) {
    SamplingConfigInfo samplingConfigInfo =
        samplingConfigDetails.getSamplingConfig().getSamplingConfigInfo();
    SamplingConfigMetadata samplingConfigMetadata = samplingConfigDetails.getMetadata();
    return zip(
        this.rateLimitConfigConverter.convert(samplingConfigInfo.getRateLimitConfig()),
        this.filterConverter.convert(samplingConfigInfo.getFilter()),
        (rateLimitConfig, spanProcessingRuleFilter) ->
            ConvertedSamplingConfig.builder()
                .id(samplingConfigDetails.getSamplingConfig().getId())
                .rateLimitConfig(rateLimitConfig)
                .spanFilter(spanProcessingRuleFilter)
                .creationTime(
                    Instant.ofEpochSecond(
                        samplingConfigMetadata.getCreationTimestamp().getSeconds(),
                        samplingConfigMetadata.getCreationTimestamp().getNanos()))
                .lastUpdatedTime(
                    Instant.ofEpochSecond(
                        samplingConfigMetadata.getLastUpdatedTimestamp().getSeconds(),
                        samplingConfigMetadata.getLastUpdatedTimestamp().getNanos()))
                .build());
  }

  @Value
  @Builder
  @Accessors(fluent = true)
  private static class ConvertedSamplingConfig implements SamplingConfig {
    String id;
    RateLimitConfig rateLimitConfig;
    SpanProcessingRuleFilter spanFilter;
    Instant creationTime;
    Instant lastUpdatedTime;
  }
}
