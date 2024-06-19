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
  @GraphQLDescription(
      "The logical operator is used to combine the list of filters provided in the spanFilters field")
  @GraphQLNonNull
  LogicalOperator logicalOperator();

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_FILTERS_KEY)
  @GraphQLDescription(
      "List of span filters to be evaluated combining together with the logical operator provided")
  @GraphQLNonNull
  List<SpanProcessingRuleFilter> spanFilters();
}
