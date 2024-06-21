package org.hypertrace.core.graphql.common.schema.results.arguments.order;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(OrderDirection.TYPE_NAME)
public enum OrderDirection {
  ASC,
  DESC;

  public static final String TYPE_NAME = "OrderDirection";
}
