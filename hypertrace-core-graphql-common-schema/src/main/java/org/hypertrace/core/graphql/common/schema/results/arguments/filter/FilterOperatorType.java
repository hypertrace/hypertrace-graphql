package org.hypertrace.core.graphql.common.schema.results.arguments.filter;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(FilterOperatorType.TYPE_NAME)
public enum FilterOperatorType {
  EQUALS,
  NOT_EQUALS,
  LESS_THAN,
  LESS_THAN_OR_EQUAL_TO,
  GREATER_THAN,
  GREATER_THAN_OR_EQUAL_TO,
  LIKE,
  IN,
  NOT_IN,
  CONTAINS_KEY,
  CONTAINS_KEY_VALUE,
  CONTAINS_KEY_LIKE,
  NOT_CONTAINS_KEY;

  public static final String TYPE_NAME = "FilterOperatorType";
}
