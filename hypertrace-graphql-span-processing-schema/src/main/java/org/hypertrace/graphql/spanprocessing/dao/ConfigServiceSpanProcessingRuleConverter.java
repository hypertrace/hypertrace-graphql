package org.hypertrace.graphql.spanprocessing.dao;

import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import java.util.NoSuchElementException;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRule;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRuleRuleType;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;
import org.hypertrace.span.processing.config.service.v1.RuleType;

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
                    convertExcludeSpanRuleRuleType(ruleDetails.getRule().getRuleInfo().getType()),
                    Instant.ofEpochSecond(
                        ruleDetails.getMetadata().getCreationTimestamp().getSeconds(),
                        ruleDetails.getMetadata().getCreationTimestamp().getNanos()),
                    Instant.ofEpochSecond(
                        ruleDetails.getMetadata().getLastUpdatedTimestamp().getSeconds(),
                        ruleDetails.getMetadata().getLastUpdatedTimestamp().getNanos())));
  }

  private ExcludeSpanRuleRuleType convertExcludeSpanRuleRuleType(RuleType ruleType) {
    switch (ruleType) {
      case RULE_TYPE_UNSPECIFIED: // required to cater for the older user configs(as they didn't
        // have a rule type field)
      case RULE_TYPE_USER:
        return ExcludeSpanRuleRuleType.USER;
      case RULE_TYPE_SYSTEM:
        return ExcludeSpanRuleRuleType.SYSTEM;
      default:
        throw new NoSuchElementException("Unsupported Exclude span rule rule type: " + ruleType);
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedExcludeSpanRule implements ExcludeSpanRule {
    String id;
    String name;
    SpanProcessingRuleFilter spanFilter;
    boolean disabled;
    ExcludeSpanRuleRuleType ruleType;
    Instant creationTime;
    Instant lastUpdatedTime;
  }
}
