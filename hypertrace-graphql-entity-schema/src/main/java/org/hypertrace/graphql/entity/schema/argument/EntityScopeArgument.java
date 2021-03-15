package org.hypertrace.graphql.entity.schema.argument;

import org.hypertrace.core.graphql.deserialization.PrimitiveArgument;

public interface EntityScopeArgument extends PrimitiveArgument<String> {
  String ARGUMENT_NAME = "scope";
}
