package org.hypertrace.core.graphql.utils.gateway;

import io.reactivex.rxjava3.core.Single;
import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
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

  private Value convertValue(Optional<Object> optionalObject) {
    if (optionalObject.isEmpty()) {
      return Value.getDefaultInstance();
    }
    Object object = optionalObject.get();

    if (this.assignableToAnyOfClasses(
        object.getClass(), Long.class, Integer.class, BigInteger.class)) {
      return Value.newBuilder()
          .setValueType(ValueType.LONG)
          .setLong(((Number) object).longValue())
          .build();
    }
    if (this.assignableToAnyOfClasses(object.getClass(), Number.class)) {
      return Value.newBuilder()
          .setValueType(ValueType.DOUBLE)
          .setDouble(((Number) object).doubleValue())
          .build();
    }
    if (this.assignableToAnyOfClasses(object.getClass(), Boolean.class)) {
      return Value.newBuilder().setValueType(ValueType.BOOL).setBoolean((Boolean) object).build();
    }
    // todo handle Instant type object
    if (this.assignableToAnyOfClasses(object.getClass(), TemporalAccessor.class)) {
      return Value.newBuilder()
          .setValueType(ValueType.TIMESTAMP)
          .setTimestamp(Instant.from((TemporalAccessor) object).toEpochMilli())
          .build();
    }
    if (this.assignableToAnyOfClasses(object.getClass(), Collection.class)) {
      // TODO matches old impl, but probably should be expanded
      return Value.newBuilder()
          .setValueType(ValueType.STRING_ARRAY)
          .addAllStringArray(
              ((Collection<?>) object)
                  .stream().map(String::valueOf).collect(Collectors.toUnmodifiableList()))
          .build();
    }

    return Value.newBuilder()
        .setValueType(ValueType.STRING)
        .setString(String.valueOf(object))
        .build();
  }

  private boolean assignableToAnyOfClasses(Class<?> classToCheck, Class<?>... classesAllowed) {
    return Arrays.stream(classesAllowed)
        .anyMatch(allowedClass -> allowedClass.isAssignableFrom(classToCheck));
  }
}
