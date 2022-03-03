package org.hypertrace.graphql.spanprocessing.deserialization;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingFilterField;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRelationalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRelationalOperator;

@Value
@Accessors(fluent = true)
@Jacksonized
@Builder
public class DefaultSpanProcessingRelationalFilter implements SpanProcessingRelationalFilter {
  String key;
  SpanProcessingFilterField field;
  SpanProcessingRelationalOperator relationalOperator;
  Object value;
}
