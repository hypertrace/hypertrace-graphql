package org.hypertrace.graphql.spanprocessing.deserialization;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingLogicalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRelationalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

@Value
@Accessors(fluent = true)
@Jacksonized
@Builder
public class DefaultSpanProcessingRuleFilter implements SpanProcessingRuleFilter {
  SpanProcessingLogicalFilter logicalSpanFilter;
  SpanProcessingRelationalFilter relationalSpanFilter;
}
