package org.hypertrace.graphql.spanprocessing.schema.rule.filter;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.operator.LogicalOperator;

@GraphQLName(SpanProcessingLogicalFilter.TYPE_NAME)
public interface SpanProcessingLogicalFilter {
  String TYPE_NAME = "SpanProcessingLogicalFilter";

  String SPAN_PROCESSING_FILTERS_KEY = "spanFilters";
  String SPAN_PROCESSING_LOGICAL_OPERATOR_KEY = "logicalOperator";

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_LOGICAL_OPERATOR_KEY)
  @GraphQLDescription("Span processing logical operator")
  @GraphQLNonNull
  LogicalOperator logicalOperator();

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_FILTERS_KEY)
  @GraphQLDescription("Span processing filters")
  @GraphQLNonNull
  List<SpanProcessingRuleFilter> spanFilters();
}
