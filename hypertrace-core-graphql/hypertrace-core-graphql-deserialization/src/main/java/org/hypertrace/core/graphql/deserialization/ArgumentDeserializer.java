package org.hypertrace.core.graphql.deserialization;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ArgumentDeserializer {

  <T> Optional<T> deserializeObject(Map<String, Object> arguments, Class<T> argSchema);

  <T> Optional<List<T>> deserializeObjectList(Map<String, Object> arguments, Class<T> argSchema);

  <T> Optional<T> deserializePrimitive(
      Map<String, Object> arguments, Class<? extends PrimitiveArgument<T>> argSchema);

  <T> Optional<List<T>> deserializePrimitiveList(
      Map<String, Object> arguments, Class<? extends PrimitiveArgument<T>> argSchema);
}
