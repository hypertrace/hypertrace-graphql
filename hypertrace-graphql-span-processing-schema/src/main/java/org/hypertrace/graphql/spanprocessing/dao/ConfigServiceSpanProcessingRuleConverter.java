package org.hypertrace.graphql.spanprocessing.dao;

import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import javax.inject.Inject;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRule;
import org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRuleConfig;
import org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRuleConfigType;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRule;
import org.hypertrace.graphql.spanprocessing.schema.rule.IncludeSpanRule;
import org.hypertrace.graphql.spanprocessing.schema.rule.SegmentMatchingBasedRuleConfig;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;
import org.hypertrace.span.processing.config.service.v1.ApiNamingRuleDetails;
import org.hypertrace.span.processing.config.service.v1.SegmentMatchingBasedConfig;

class ConfigServiceSpanProcessingRuleConverter {

  private final ConfigServiceSpanFilterConverter filterConverter;

  @Inject
  ConfigServiceSpanProcessingRuleConverter(ConfigServiceSpanFilterConverter filterConverter) {
    this.filterConverter = filterConverter;
  }

  public Single<ExcludeSpanRule> convert(
      org.hypertrace.span.processing.config.service.v1.ExcludeSpanRuleDetails ruleDetails) {
    return this.filterConverter
        .convert(ruleDetails.getRule().getRuleInfo().getFilter())
        .map(
            spanProcessingRuleFilter ->
                new ConvertedExcludeSpanRule(
                    ruleDetails.getRule().getId(),
                    ruleDetails.getRule().getRuleInfo().getName(),
                    spanProcessingRuleFilter,
                    ruleDetails.getRule().getRuleInfo().getDisabled(),
                    Instant.ofEpochSecond(
                        ruleDetails.getMetadata().getCreationTimestamp().getSeconds(),
                        ruleDetails.getMetadata().getCreationTimestamp().getNanos()),
                    Instant.ofEpochSecond(
                        ruleDetails.getMetadata().getLastUpdatedTimestamp().getSeconds(),
                        ruleDetails.getMetadata().getLastUpdatedTimestamp().getNanos())));
  }

  public Single<IncludeSpanRule> convert(
      org.hypertrace.span.processing.config.service.v1.IncludeSpanRuleDetails ruleDetails) {
    return this.filterConverter
        .convert(ruleDetails.getRule().getRuleInfo().getFilter())
        .map(
            spanProcessingRuleFilter ->
                ConvertedIncludeSpanRule.builder()
                    .id(ruleDetails.getRule().getId())
                    .name(ruleDetails.getRule().getRuleInfo().getName())
                    .spanFilter(spanProcessingRuleFilter)
                    .disabled(ruleDetails.getRule().getRuleInfo().getDisabled())
                    .creationTime(
                        Instant.ofEpochSecond(
                            ruleDetails.getMetadata().getCreationTimestamp().getSeconds(),
                            ruleDetails.getMetadata().getCreationTimestamp().getNanos()))
                    .lastUpdatedTime(
                        Instant.ofEpochSecond(
                            ruleDetails.getMetadata().getLastUpdatedTimestamp().getSeconds(),
                            ruleDetails.getMetadata().getLastUpdatedTimestamp().getNanos()))
                    .build());
  }

  public Single<ApiNamingRule> convert(ApiNamingRuleDetails ruleDetails) {
    return this.filterConverter
        .convert(ruleDetails.getRule().getRuleInfo().getFilter())
        .map(
            spanProcessingRuleFilter ->
                new ConvertedApiNamingRule(
                    ruleDetails.getRule().getId(),
                    ruleDetails.getRule().getRuleInfo().getName(),
                    spanProcessingRuleFilter,
                    ruleDetails.getRule().getRuleInfo().getDisabled(),
                    convertApiNamingRuleConfig(ruleDetails.getRule().getRuleInfo().getRuleConfig()),
                    Instant.ofEpochSecond(
                        ruleDetails.getMetadata().getCreationTimestamp().getSeconds(),
                        ruleDetails.getMetadata().getCreationTimestamp().getNanos()),
                    Instant.ofEpochSecond(
                        ruleDetails.getMetadata().getLastUpdatedTimestamp().getSeconds(),
                        ruleDetails.getMetadata().getLastUpdatedTimestamp().getNanos())));
  }

  private ApiNamingRuleConfig convertApiNamingRuleConfig(
      org.hypertrace.span.processing.config.service.v1.ApiNamingRuleConfig apiNamingRuleConfig) {
    switch (apiNamingRuleConfig.getRuleConfigCase()) {
      case SEGMENT_MATCHING_BASED_CONFIG:
        SegmentMatchingBasedConfig segmentMatchingBasedConfig =
            apiNamingRuleConfig.getSegmentMatchingBasedConfig();
        return new ConvertedApiNamingRuleConfig(
            ApiNamingRuleConfigType.SEGMENT_MATCHING,
            new ConvertedSegmentMatchingBasedRuleConfig(
                segmentMatchingBasedConfig.getRegexesList(),
                segmentMatchingBasedConfig.getValuesList()));
      default:
        throw new NoSuchElementException("Unsupported Rule config type: " + apiNamingRuleConfig);
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedExcludeSpanRule implements ExcludeSpanRule {
    String id;
    String name;
    SpanProcessingRuleFilter spanFilter;
    boolean disabled;
    Instant creationTime;
    Instant lastUpdatedTime;
  }

  @Value
  @Builder
  @Accessors(fluent = true)
  private static class ConvertedIncludeSpanRule implements IncludeSpanRule {
    String id;
    String name;
    SpanProcessingRuleFilter spanFilter;
    boolean disabled;
    Instant creationTime;
    Instant lastUpdatedTime;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedApiNamingRule implements ApiNamingRule {
    String id;
    String name;
    SpanProcessingRuleFilter spanFilter;
    boolean disabled;
    ApiNamingRuleConfig apiNamingRuleConfig;
    Instant creationTime;
    Instant lastUpdatedTime;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedApiNamingRuleConfig implements ApiNamingRuleConfig {
    ApiNamingRuleConfigType apiNamingRuleConfigType;
    SegmentMatchingBasedRuleConfig segmentMatchingBasedRuleConfig;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedSegmentMatchingBasedRuleConfig
      implements SegmentMatchingBasedRuleConfig {
    List<String> regexes;
    List<String> values;
  }
}
