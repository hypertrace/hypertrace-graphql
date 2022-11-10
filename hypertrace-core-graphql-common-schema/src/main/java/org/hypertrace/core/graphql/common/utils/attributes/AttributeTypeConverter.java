package org.hypertrace.core.graphql.common.utils.attributes;

import static org.hypertrace.core.graphql.attributes.AttributeModelType.BOOLEAN;
import static org.hypertrace.core.graphql.attributes.AttributeModelType.DOUBLE;
import static org.hypertrace.core.graphql.attributes.AttributeModelType.LONG;
import static org.hypertrace.core.graphql.attributes.AttributeModelType.STRING;
import static org.hypertrace.core.graphql.attributes.AttributeModelType.STRING_ARRAY;
import static org.hypertrace.core.graphql.attributes.AttributeModelType.STRING_MAP;
import static org.hypertrace.core.graphql.attributes.AttributeModelType.TIMESTAMP;

import com.google.common.collect.ImmutableBiMap;
import io.reactivex.rxjava3.core.Single;
import java.util.Optional;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.attributes.AttributeModelType;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeType;
import org.hypertrace.core.graphql.common.utils.Converter;

public class AttributeTypeConverter implements Converter<AttributeModelType, AttributeType> {
  private static final ImmutableBiMap<AttributeModelType, AttributeType> TYPE_MAPPING =
      ImmutableBiMap.<AttributeModelType, AttributeType>builder()
          .put(STRING, AttributeType.STRING)
          .put(BOOLEAN, AttributeType.BOOLEAN)
          .put(LONG, AttributeType.LONG)
          .put(DOUBLE, AttributeType.DOUBLE)
          .put(TIMESTAMP, AttributeType.TIMESTAMP)
          .put(STRING_MAP, AttributeType.STRING_MAP)
          .put(STRING_ARRAY, AttributeType.STRING_ARRAY)
          .build();

  @Override
  public Single<AttributeType> convert(AttributeModelType type) {
    return Optional.ofNullable(TYPE_MAPPING.get(type))
        .map(Single::just)
        .orElseGet(
            () ->
                Single.error(
                    new UnknownFormatConversionException(
                        String.format("Unrecognized attribute type %s", type.name()))));
  }

  public Single<AttributeModelType> convert(final AttributeType type) {
    return Optional.ofNullable(TYPE_MAPPING.inverse().get(type))
        .map(Single::just)
        .orElseGet(
            () ->
                Single.error(
                    new UnknownFormatConversionException(
                        String.format("Unrecognized attribute type %s", type.name()))));
  }
}
