package org.hypertrace.core.graphql.utils.gateway;

import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import java.util.Optional;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.common.ValueType;

class UnwrappedValueConverter
    implements Converter<Value, Object>, BiConverter<Value, AttributeModel, Object> {

  @Override
  public Single<Object> convert(Value from) {
    Value value = Optional.ofNullable(from).orElse(Value.getDefaultInstance());
    switch (value.getValueType()) {
      case STRING:
        return Single.just(value.getString());
      case BOOL:
        return Single.just(value.getBoolean());
      case LONG:
        return Single.just(value.getLong());
      case DOUBLE:
        return Single.just(value.getDouble());
      case TIMESTAMP:
        try {
          return Single.just(Instant.ofEpochMilli(value.getTimestamp()));
        } catch (Throwable t) {
          return Single.error(t);
        }
      case STRING_MAP:
        return Single.just(value.getStringMapMap());
      case STRING_ARRAY:
        return Single.just(value.getStringArrayList());
      case LONG_ARRAY:
      case DOUBLE_ARRAY:
      case BOOLEAN_ARRAY:
      case UNSET:
      case UNRECOGNIZED:
      default:
        return Single.error(
            new UnsupportedOperationException(
                String.format(
                    "Cannot convert value for unknown value type '%s'",
                    value.getValueType().name())));
    }
  }

  @Override
  public Single<Object> convert(Value from, AttributeModel attributeModel) {
    Value value = Optional.ofNullable(from).orElse(Value.getDefaultInstance());
    if (ValueType.TIMESTAMP.equals(value.getValueType())) {
      return handleTimestamp(value, attributeModel);
    }
    return convert(from);
  }

  private Single<Object> handleTimestamp(Value value, AttributeModel attributeModel) {
    try {
      if ("ns".equals(attributeModel.units())) {
        return Single.just(Instant.ofEpochSecond(0, value.getTimestamp()));
      }
      return Single.just(Instant.ofEpochMilli(value.getTimestamp()));
    } catch (Throwable t) {
      return Single.error(t);
    }
  }
}
