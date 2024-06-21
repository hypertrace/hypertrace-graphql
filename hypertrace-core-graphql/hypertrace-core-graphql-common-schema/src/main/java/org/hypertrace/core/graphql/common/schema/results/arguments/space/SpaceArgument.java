package org.hypertrace.core.graphql.common.schema.results.arguments.space;

import org.hypertrace.core.graphql.deserialization.PrimitiveArgument;

public interface SpaceArgument extends PrimitiveArgument<String> {
  String ARGUMENT_NAME = "space";
}
