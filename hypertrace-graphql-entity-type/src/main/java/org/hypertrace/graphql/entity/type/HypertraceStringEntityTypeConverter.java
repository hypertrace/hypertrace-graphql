package org.hypertrace.graphql.entity.type;

import io.reactivex.rxjava3.core.Single;
import java.util.Optional;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.graphql.atttribute.scopes.HypertraceAttributeScopeString;
import org.hypertrace.graphql.entity.schema.EntityType;

class HypertraceStringEntityTypeConverter implements Converter<String, Optional<EntityType>> {

  @Override
  public Single<Optional<EntityType>> convert(String entityTypeString) {
    switch (entityTypeString) {
      case HypertraceAttributeScopeString.API:
        return Single.just(Optional.of(HypertraceEntityType.API));
      case HypertraceAttributeScopeString.SERVICE:
        return Single.just(Optional.of(HypertraceEntityType.SERVICE));
      case HypertraceAttributeScopeString.BACKEND:
        return Single.just(Optional.of(HypertraceEntityType.BACKEND));
      default:
        return Single.just(Optional.empty());
    }
  }
}
