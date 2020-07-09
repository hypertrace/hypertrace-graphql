package org.hypertrace.graphql.entity.type;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.graphql.entity.schema.EntityType;

class HypertraceEntityTypeScopeConverter implements Converter<EntityType, AttributeModelScope> {

  @Override
  public Single<AttributeModelScope> convert(EntityType entityType) {
    switch ((HypertraceEntityType) entityType) {
      case API:
        return Single.just(AttributeModelScope.API);
      case SERVICE:
        return Single.just(AttributeModelScope.SERVICE);
      case BACKEND:
        return Single.just(AttributeModelScope.BACKEND);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format("Unable to convert unknown entity type '%s'", entityType)));
    }
  }
}
