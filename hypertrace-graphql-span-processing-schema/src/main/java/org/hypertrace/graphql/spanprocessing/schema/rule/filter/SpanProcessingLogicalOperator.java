package org.hypertrace.graphql.spanprocessing.schema.rule.filter;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(SpanProcessingLogicalOperator.TYPE_NAME)
public enum SpanProcessingLogicalOperator {
  AND,
  OR;
  static final String TYPE_NAME = "SpanProcessingLogicalOperator";
}
