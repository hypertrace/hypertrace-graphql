package org.hypertrace.core.graphql.common.schema.operator;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLDescription("Logical operator describing how to combine multiple independent clauses")
@GraphQLName(LogicalOperator.TYPE_NAME)
public enum LogicalOperator {
  AND,
  OR;
  static final String TYPE_NAME = "LogicalOperator";
}
