package org.hypertrace.core.graphql.common.schema.results.arguments.page;

import org.hypertrace.core.graphql.deserialization.PrimitiveArgument;

public interface OffsetArgument extends PrimitiveArgument<Integer> {
  String ARGUMENT_NAME = "offset";
}
