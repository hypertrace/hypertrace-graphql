package org.hypertrace.core.graphql.deserialization;

import lombok.Value;

@Value
class SimpleArgumentDeserializationConfig implements ArgumentDeserializationConfig {
  String argumentKey;
  Class<?> argumentSchema;
}
