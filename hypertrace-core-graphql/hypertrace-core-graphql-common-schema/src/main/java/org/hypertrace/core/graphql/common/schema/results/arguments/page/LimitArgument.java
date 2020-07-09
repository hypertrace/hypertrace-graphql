package org.hypertrace.core.graphql.common.schema.results.arguments.page;

import org.hypertrace.core.graphql.deserialization.PrimitiveArgument;

public interface LimitArgument extends PrimitiveArgument<Integer> {
  String ARGUMENT_NAME = "limit";
}
