package org.hypertrace.graphql.entity.schema.argument;

import org.hypertrace.core.graphql.deserialization.PrimitiveArgument;

public interface IncludeNonLiveEntitiesArgument extends PrimitiveArgument<Boolean> {
  String ARGUMENT_NAME = "includeNonLiveEntities";
}
