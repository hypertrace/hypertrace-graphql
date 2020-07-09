package org.hypertrace.graphql.entity.type;

import static org.hypertrace.graphql.entity.type.HypertraceEntityType.API;
import static org.hypertrace.graphql.entity.type.HypertraceEntityType.BACKEND;
import static org.hypertrace.graphql.entity.type.HypertraceEntityType.SERVICE;
import static org.hypertrace.graphql.entity.type.HypertraceEntityTypeStringConverter.WELL_KNOWN_STRING_API;
import static org.hypertrace.graphql.entity.type.HypertraceEntityTypeStringConverter.WELL_KNOWN_STRING_BACKEND;
import static org.hypertrace.graphql.entity.type.HypertraceEntityTypeStringConverter.WELL_KNOWN_STRING_SERVICE;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.graphql.entity.schema.EntityType;

class HypertraceStringEntityTypeConverter implements Converter<String, EntityType> {

  @Override
  public Single<EntityType> convert(String entityTypeString) {
    switch (entityTypeString) {
      case WELL_KNOWN_STRING_API:
        return Single.just(API);
      case WELL_KNOWN_STRING_SERVICE:
        return Single.just(SERVICE);
      case WELL_KNOWN_STRING_BACKEND:
        return Single.just(BACKEND);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format(
                    "Unable to convert unknown entity type string '%s'", entityTypeString)));
    }
  }
}
