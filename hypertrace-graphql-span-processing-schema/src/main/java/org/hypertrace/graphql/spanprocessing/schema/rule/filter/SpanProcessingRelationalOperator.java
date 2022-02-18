package org.hypertrace.graphql.spanprocessing.schema.rule.filter;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(SpanProcessingRelationalOperator.TYPE_NAME)
public enum SpanProcessingRelationalOperator {
  EQUALS,
  NOT_EQUALS,
  CONTAINS,
  STARTS_WITH,
  ENDS_WITH,
  REGEX_MATCH;
  static final String TYPE_NAME = "SpanProcessingRelationalOperator";
}
