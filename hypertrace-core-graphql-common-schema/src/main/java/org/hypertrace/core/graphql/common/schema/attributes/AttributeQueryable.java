package org.hypertrace.core.graphql.common.schema.attributes;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeKeyArgument;

public interface AttributeQueryable {

  String ATTRIBUTE_FIELD_NAME = "attribute";

  @GraphQLField
  @GraphQLName(ATTRIBUTE_FIELD_NAME)
  Object attribute(@GraphQLName(AttributeKeyArgument.ARGUMENT_NAME) @GraphQLNonNull String key);
}
