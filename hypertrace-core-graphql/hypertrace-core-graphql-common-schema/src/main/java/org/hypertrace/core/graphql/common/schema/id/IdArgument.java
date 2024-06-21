package org.hypertrace.core.graphql.common.schema.id;

import org.hypertrace.core.graphql.deserialization.PrimitiveArgument;

public interface IdArgument extends PrimitiveArgument<String> {
  String ARGUMENT_NAME = "id";
}
