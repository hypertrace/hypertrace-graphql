package org.hypertrace.graphql.spanprocessing.dao;

import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRule;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

class ConfigServiceSpanProcessingRuleConverter
    implements Converter<
        org.hypertrace.span.processing.config.service.v1.ExcludeSpanRuleDetails, ExcludeSpanRule> {

  private final ConfigServiceSpanFilterConverter filterConverter;

  @Inject
  ConfigServiceSpanProcessingRuleConverter(ConfigServiceSpanFilterConverter filterConverter) {
    this.filterConverter = filterConverter;
  }

  @Override
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
}
