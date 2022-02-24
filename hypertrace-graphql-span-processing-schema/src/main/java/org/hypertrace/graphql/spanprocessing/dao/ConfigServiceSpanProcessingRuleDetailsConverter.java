package org.hypertrace.graphql.spanprocessing.dao;

import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRuleDetails;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

class ConfigServiceSpanProcessingRuleDetailsConverter
    implements Converter<
        org.hypertrace.span.processing.config.service.v1.ExcludeSpanRuleDetails,
        ExcludeSpanRuleDetails> {

  private final ConfigServiceSpanFilterConverter filterConverter;

  @Inject
  ConfigServiceSpanProcessingRuleDetailsConverter(
      ConfigServiceSpanFilterConverter filterConverter) {
    this.filterConverter = filterConverter;
  }

  @Override
  public Single<ExcludeSpanRuleDetails> convert(
      org.hypertrace.span.processing.config.service.v1.ExcludeSpanRuleDetails ruleDetails) {
    return this.filterConverter
        .convert(ruleDetails.getRule().getRuleInfo().getFilter())
        .map(
            spanProcessingRuleFilter ->
                new ConvertedExcludeSpanRuleDetails(
                    ruleDetails.getRule().getId(),
                    ruleDetails.getRule().getRuleInfo().getName(),
                    spanProcessingRuleFilter,
                    Instant.ofEpochSecond(
                        ruleDetails.getMetadata().getCreationTimestamp().getSeconds(),
                        ruleDetails.getMetadata().getCreationTimestamp().getNanos()),
                    Instant.ofEpochSecond(
                        ruleDetails.getMetadata().getLastUpdatedTimestamp().getSeconds(),
                        ruleDetails.getMetadata().getLastUpdatedTimestamp().getNanos())));
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedExcludeSpanRuleDetails implements ExcludeSpanRuleDetails {
    String id;
    String name;
    SpanProcessingRuleFilter spanFilter;
    Instant creationTime;
    Instant lastUpdatedTime;
  }
}
