package org.hypertrace.core.graphql.utils.gateway;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.hypertrace.gateway.service.v1.common.ValueType.BOOL;
import static org.hypertrace.gateway.service.v1.common.ValueType.BOOLEAN_ARRAY;
import static org.hypertrace.gateway.service.v1.common.ValueType.DOUBLE;
import static org.hypertrace.gateway.service.v1.common.ValueType.DOUBLE_ARRAY;
import static org.hypertrace.gateway.service.v1.common.ValueType.LONG;
import static org.hypertrace.gateway.service.v1.common.ValueType.LONG_ARRAY;
import static org.hypertrace.gateway.service.v1.common.ValueType.STRING;
import static org.hypertrace.gateway.service.v1.common.ValueType.STRING_ARRAY;
import static org.hypertrace.gateway.service.v1.common.ValueType.TIMESTAMP;

import io.reactivex.rxjava3.core.Single;
import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.LiteralConstant;
import org.hypertrace.gateway.service.v1.common.LiteralConstant.Builder;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.common.ValueType;

class LiteralConstantConverter implements Converter<Object, LiteralConstant> {
  @Override
  public Single<LiteralConstant> convert(Object from) {
    return Single.just(Optional.ofNullable(from))
        .map(this::convertValue)
        .map(LiteralConstant.newBuilder()::setValue)
        .map(Builder::build);
  }

  private Value convertValue(
      @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Object> optionalObject) {
    if (optionalObject.isEmpty()) {
      return Value.getDefaultInstance();
    }
    Object object = optionalObject.get();
    final ValueType valueType = getValueType(object);
    final Value.Builder valueBuilder = Value.newBuilder().setValueType(valueType);

    switch (valueType) {
      case LONG:
        return valueBuilder.setLong(((Number) object).longValue()).build();

      case DOUBLE:
        return valueBuilder.setDouble(((Number) object).doubleValue()).build();

      case BOOL:
        return valueBuilder.setBoolean((Boolean) object).build();

      case TIMESTAMP:
        return valueBuilder
            .setTimestamp(Instant.from((TemporalAccessor) object).toEpochMilli())
            .build();

      case BOOLEAN_ARRAY:
        return valueBuilder
            .addAllBooleanArray(
                ((Collection<?>) object)
                    .stream().map(obj -> (Boolean) obj).collect(toUnmodifiableList()))
            .build();

      case LONG_ARRAY:
        return valueBuilder
            .addAllLongArray(
                ((Collection<?>) object)
                    .stream().map(obj -> ((Number) obj).longValue()).collect(toUnmodifiableList()))
            .build();

      case DOUBLE_ARRAY:
        return valueBuilder
            .addAllDoubleArray(
                ((Collection<?>) object)
                    .stream()
                        .map(obj -> ((Number) obj).doubleValue())
                        .collect(toUnmodifiableList()))
            .build();

      case STRING_ARRAY:
        return valueBuilder
            .addAllStringArray(
                ((Collection<?>) object)
                    .stream().map(String::valueOf).collect(toUnmodifiableList()))
            .build();
    }

    return valueBuilder.setString(String.valueOf(object)).build();
  }

  private boolean assignableToAnyOfClasses(Class<?> classToCheck, Class<?>... classesAllowed) {
    return Arrays.stream(classesAllowed)
        .anyMatch(allowedClass -> allowedClass.isAssignableFrom(classToCheck));
  }

  private ValueType getValueType(final Object object) {
    if (this.assignableToAnyOfClasses(
        object.getClass(), Long.class, Integer.class, BigInteger.class)) {
      return LONG;
    }
    if (this.assignableToAnyOfClasses(object.getClass(), Number.class)) {
      return DOUBLE;
    }
    if (this.assignableToAnyOfClasses(object.getClass(), Boolean.class)) {
      return BOOL;
    }
    // todo handle Instant type object
    if (this.assignableToAnyOfClasses(object.getClass(), TemporalAccessor.class)) {
      return TIMESTAMP;
    }

    if (this.assignableToAnyOfClasses(object.getClass(), Collection.class)) {
      return getCollectionType(object);
    }

    return STRING;
  }

  private ValueType getCollectionType(final Object object) {
    final Collection<?> collection = (Collection<?>) object;
    if (collection.isEmpty()) {
      return STRING_ARRAY;
    }

    final Object first = collection.iterator().next();
    final ValueType baseType = getValueType(first);

    switch (baseType) {
      case BOOL:
        return BOOLEAN_ARRAY;

      case LONG:
        return LONG_ARRAY;

      case DOUBLE:
        return DOUBLE_ARRAY;
    }

    return STRING_ARRAY;
  }
}
