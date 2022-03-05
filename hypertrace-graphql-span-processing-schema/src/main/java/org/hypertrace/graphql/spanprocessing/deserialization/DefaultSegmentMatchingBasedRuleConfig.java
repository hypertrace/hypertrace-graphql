package org.hypertrace.graphql.spanprocessing.deserialization;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.graphql.spanprocessing.schema.rule.SegmentMatchingBasedRuleConfig;

@Value
@Accessors(fluent = true)
@Jacksonized
@Builder
public class DefaultSegmentMatchingBasedRuleConfig implements SegmentMatchingBasedRuleConfig {
  List<String> regexes;
  List<String> values;
}
