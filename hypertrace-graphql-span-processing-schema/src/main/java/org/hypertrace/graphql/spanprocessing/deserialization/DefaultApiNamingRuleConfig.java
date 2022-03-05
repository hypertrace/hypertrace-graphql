package org.hypertrace.graphql.spanprocessing.deserialization;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRuleConfig;
import org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRuleConfigType;
import org.hypertrace.graphql.spanprocessing.schema.rule.SegmentMatchingBasedRuleConfig;

@Value
@Accessors(fluent = true)
@Jacksonized
@Builder
public class DefaultApiNamingRuleConfig implements ApiNamingRuleConfig {
  ApiNamingRuleConfigType apiNamingRuleConfigType;
  SegmentMatchingBasedRuleConfig segmentMatchingBasedRuleConfig;
}
