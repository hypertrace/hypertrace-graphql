package org.hypertrace.core.graphql.common.schema.attributes;

import static java.util.Objects.requireNonNull;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import java.util.Optional;
import javax.annotation.Nullable;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeKeyArgument;

public interface AttributeQueryable {

  String ATTRIBUTE_FIELD_NAME = "attribute";

  @GraphQLField
  @GraphQLName(ATTRIBUTE_FIELD_NAME)
  default Object attribute(
      @Deprecated @GraphQLName(AttributeKeyArgument.ARGUMENT_NAME) @Nullable String key,
      @GraphQLName(AttributeExpression.ARGUMENT_NAME) @Nullable AttributeExpression expression) {
    return attribute(
        Optional.ofNullable(expression)
            .orElseGet(() -> AttributeExpression.forAttributeKey(requireNonNull(key))));
  }

  // Once callers are migrated off using the string, we'll remove it and use this api only
  Object attribute(@GraphQLName(AttributeExpression.ARGUMENT_NAME) AttributeExpression expression);
}
