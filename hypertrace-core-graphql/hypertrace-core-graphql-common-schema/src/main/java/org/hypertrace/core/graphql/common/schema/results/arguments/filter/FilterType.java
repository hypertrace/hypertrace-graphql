package org.hypertrace.core.graphql.common.schema.results.arguments.filter;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(FilterType.TYPE_NAME)
public enum FilterType {
  @Deprecated
  STRING,
  @Deprecated
  NUMERIC,
  ATTRIBUTE,
  ID;

  public static final String TYPE_NAME = "FilterType";
}
