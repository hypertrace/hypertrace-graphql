package org.hypertrace.core.graphql.common.schema.attributes.arguments;

import org.hypertrace.core.graphql.deserialization.PrimitiveArgument;

public interface AttributeKeyArgument extends PrimitiveArgument<String> {
  String ARGUMENT_NAME = "key";
}
