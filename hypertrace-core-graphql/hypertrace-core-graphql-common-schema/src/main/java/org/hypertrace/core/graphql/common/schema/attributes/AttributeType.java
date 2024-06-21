package org.hypertrace.core.graphql.common.schema.attributes;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(AttributeType.TYPE_NAME)
public enum AttributeType {
  STRING,
  BOOLEAN,
  LONG,
  DOUBLE,
  TIMESTAMP,
  STRING_MAP,
  STRING_ARRAY,
  DOUBLE_ARRAY,
  LONG_ARRAY,
  BOOLEAN_ARRAY;

  public static final String TYPE_NAME = "AttributeType";
}
