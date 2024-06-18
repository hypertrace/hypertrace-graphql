package org.hypertrace.graphql.spanprocessing.schema.rule.filter;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(SpanProcessingRuleFilter.TYPE_NAME)
public interface SpanProcessingRuleFilter {
  String TYPE_NAME = "SpanProcessingRuleFilter";

  String SPAN_PROCESSING_LOGICAL_FILTER_KEY = "logicalSpanFilter";
  String SPAN_PROCESSING_RELATIONAL_FILTER_KEY = "relationalSpanFilter";

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_LOGICAL_FILTER_KEY)
  @GraphQLDescription(
      "Span processing logical filter containing list of spanFilters and the logical operator")
  SpanProcessingLogicalFilter logicalSpanFilter();

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_RELATIONAL_FILTER_KEY)
  @GraphQLDescription(
      "Span processing relational filter which takes in lhs and lhs, combining them with relational operator")
  SpanProcessingRelationalFilter relationalSpanFilter();
}
