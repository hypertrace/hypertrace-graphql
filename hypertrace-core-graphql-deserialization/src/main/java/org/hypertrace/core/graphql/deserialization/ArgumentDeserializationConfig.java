package org.hypertrace.core.graphql.deserialization;

import com.fasterxml.jackson.databind.Module;
import java.util.Collections;
import java.util.List;

public interface ArgumentDeserializationConfig {

  String getArgumentKey();

  default String getListArgumentKey() {
    return getArgumentKey();
  }

  Class<?> getArgumentSchema();

  default List<Module> jacksonModules() {
    return Collections.emptyList();
  }

  static ArgumentDeserializationConfig forPrimitive(String argKey, Class<?> argSchema) {
    return forPrimitive(argKey, argKey, argSchema);
  }

  static ArgumentDeserializationConfig forPrimitive(
      String argKey, String listArgKey, Class<?> argSchema) {
    return new SimpleArgumentDeserializationConfig(argKey, listArgKey, argSchema);
  }
}
