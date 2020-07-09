package org.hypertrace.graphql.entity.type;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.graphql.entity.schema.EntityType;

class HypertraceEntityTypeStringConverter implements Converter<EntityType, String> {
  static final String WELL_KNOWN_STRING_API = "API";
  static final String WELL_KNOWN_STRING_SERVICE = "SERVICE";
  static final String WELL_KNOWN_STRING_BACKEND = "BACKEND";

  @Override
  public Single<String> convert(EntityType entityType) {
    switch ((HypertraceEntityType) entityType) {
      case API:
        return Single.just(WELL_KNOWN_STRING_API);
      case SERVICE:
        return Single.just(WELL_KNOWN_STRING_SERVICE);
      case BACKEND:
        return Single.just(WELL_KNOWN_STRING_BACKEND);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format("Unable to convert unknown entity type '%s'", entityType)));
    }
  }
}
