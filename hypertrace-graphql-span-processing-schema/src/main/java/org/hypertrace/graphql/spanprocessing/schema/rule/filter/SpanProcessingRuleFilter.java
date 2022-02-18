package org.hypertrace.graphql.spanprocessing.schema.rule.filter;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(SpanProcessingRuleFilter.TYPE_NAME)
public interface SpanProcessingRuleFilter {
  String TYPE_NAME = "SpanProcessingRuleFilter";

  String SPAN_PROCESSING_LOGICAL_FILTER_KEY = "logicalSpanFilter";
  String SPAN_PROCESSING_RELATIONAL_FILTER_KEY = "relationalSpanFilter";

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_LOGICAL_FILTER_KEY)
  SpanProcessingLogicalFilter logicalSpanFilter();

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_RELATIONAL_FILTER_KEY)
  SpanProcessingRelationalFilter relationalSpanFilter();
}
