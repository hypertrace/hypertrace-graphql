package org.hypertrace.core.graphql.common.utils.attributes;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.attributes.AttributeModelType;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeType;
import org.hypertrace.core.graphql.common.utils.Converter;

class AttributeTypeConverter implements Converter<AttributeModelType, AttributeType> {

  @Override
  public Single<AttributeType> convert(AttributeModelType type) {
    switch (type) {
      case STRING:
        return Single.just(AttributeType.STRING);
      case BOOLEAN:
        return Single.just(AttributeType.BOOLEAN);
      case LONG:
        return Single.just(AttributeType.LONG);
      case DOUBLE:
        return Single.just(AttributeType.DOUBLE);
      case TIMESTAMP:
        return Single.just(AttributeType.TIMESTAMP);
      case STRING_MAP:
        return Single.just(AttributeType.STRING_MAP);
      case STRING_ARRAY:
        return Single.just(AttributeType.STRING_ARRAY);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format("Unrecognized attribute type %s", type.name())));
    }
  }
}
