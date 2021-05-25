package org.hypertrace.core.graphql.deserialization;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DefaultArgumentDeserializer implements ArgumentDeserializer {

  private static final List<Module> defaultModules =
      List.of(new JavaTimeModule(), new Jdk8Module());
  private static final Logger LOG = LoggerFactory.getLogger(DefaultArgumentDeserializer.class);

  private final ObjectMapper objectMapper;
  private final Map<Class<?>, ArgumentDeserializationConfig> configBySchema;

  @Inject
  DefaultArgumentDeserializer(Set<ArgumentDeserializationConfig> argumentDeserializationConfigs) {
    Set<Module> jacksonModules =
        Stream.concat(
                defaultModules.stream(),
                argumentDeserializationConfigs.stream()
                    .map(ArgumentDeserializationConfig::jacksonModules)
                    .flatMap(Collection::stream))
            .collect(Collectors.toUnmodifiableSet());

    this.configBySchema =
        argumentDeserializationConfigs.stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    ArgumentDeserializationConfig::getArgumentSchema, Function.identity()));

    this.objectMapper = JsonMapper.builder().addModules(jacksonModules).build();
  }

  @Override
  public <T> Optional<T> deserializeObject(Map<String, Object> arguments, Class<T> argSchema) {
    return this.argumentKey(argSchema)
        .flatMap(key -> this.deserializeValue(arguments.get(key), argSchema));
  }

  @Override
  public <T> Optional<List<T>> deserializeObjectList(
      Map<String, Object> arguments, Class<T> argSchema) {
    return this.listArgumentKey(argSchema)
        .flatMap(key -> this.deserializeValueList(arguments.get(key), argSchema));
  }

  @Override
  public <T> Optional<T> deserializePrimitive(
      Map<String, Object> arguments, Class<? extends PrimitiveArgument<T>> argSchema) {
    return this.argumentKey(argSchema).map(key -> this.uncheckedCast(arguments.get(key)));
  }

  @Override
  public <T> Optional<List<T>> deserializePrimitiveList(
      Map<String, Object> arguments, Class<? extends PrimitiveArgument<T>> argSchema) {
    return this.listArgumentKey(argSchema).flatMap(key -> this.logIfNotList(arguments.get(key)));
  }

  private <T> Optional<T> deserializeValue(@Nullable Object rawValue, Class<T> argSchema) {
    try {
      return Optional.ofNullable(rawValue)
          .map(presentRawValue -> this.objectMapper.convertValue(presentRawValue, argSchema));
    } catch (Throwable t) {
      LOG.warn("Failed to deserialize for argument type '{}'", argSchema, t);
      return Optional.empty();
    }
  }

  private Optional<String> argumentKey(Class<?> argSchema) {
    return configForSchema(argSchema).map(ArgumentDeserializationConfig::getArgumentKey);
  }

  private Optional<String> listArgumentKey(Class<?> argSchema) {
    return configForSchema(argSchema).map(ArgumentDeserializationConfig::getListArgumentKey);
  }

  private Optional<ArgumentDeserializationConfig> configForSchema(Class<?> argSchema) {
    return Optional.ofNullable(this.configBySchema.get(argSchema))
        .or(
            () -> {
              LOG.warn(
                  "No deserialization config registered for provided class '{}'",
                  argSchema.getCanonicalName());

              return Optional.empty();
            });
  }

  private <T> Optional<List<T>> deserializeValueList(
      @Nullable Object rawValueList, Class<T> argSchema) {
    try {
      return this.logIfNotList(rawValueList)
          .map(
              list ->
                  list.stream()
                      .map(rawValue -> this.deserializeValue(rawValue, argSchema).orElseThrow())
                      .collect(Collectors.toUnmodifiableList()));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private <T> Optional<List<T>> logIfNotList(@Nullable Object object) {
    if (object instanceof List) {
      return Optional.of(this.uncheckedCast(object));
    }
    if (Objects.nonNull(object)) {
      LOG.warn("Expected to deserialize list but instead got {}", object);
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  private <T> T uncheckedCast(Object value) {
    return (T) value;
  }
}
