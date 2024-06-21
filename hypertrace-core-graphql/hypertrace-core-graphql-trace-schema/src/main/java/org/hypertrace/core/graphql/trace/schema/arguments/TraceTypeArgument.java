package org.hypertrace.core.graphql.trace.schema.arguments;

import org.hypertrace.core.graphql.deserialization.PrimitiveArgument;

public interface TraceTypeArgument extends PrimitiveArgument<TraceType> {
  // TODO figure out how to separate schema definition fo types
  String ARGUMENT_NAME = "type";
}
