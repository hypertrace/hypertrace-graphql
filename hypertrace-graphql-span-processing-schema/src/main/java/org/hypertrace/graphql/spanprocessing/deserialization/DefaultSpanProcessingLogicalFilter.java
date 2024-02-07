package org.hypertrace.graphql.spanprocessing.deserialization;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.core.graphql.common.schema.operator.LogicalOperator;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingLogicalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

@Value
@Accessors(fluent = true)
@Jacksonized
@Builder
public class DefaultSpanProcessingLogicalFilter implements SpanProcessingLogicalFilter {
  LogicalOperator logicalOperator;
  List<SpanProcessingRuleFilter> spanFilters;
}
