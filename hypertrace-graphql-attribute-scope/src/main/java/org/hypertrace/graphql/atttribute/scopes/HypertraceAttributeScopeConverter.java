package org.hypertrace.graphql.atttribute.scopes;

import io.reactivex.rxjava3.core.Single;
import java.util.Optional;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.utils.Converter;

class HypertraceAttributeScopeConverter implements Converter<String, Optional<AttributeScope>> {

  @Override
  public Single<Optional<AttributeScope>> convert(String scope) {
    switch (scope) {
      case HypertraceAttributeScopeString.TRACE:
        return Single.just(Optional.of(HypertraceAttributeScope.TRACE));
      case HypertraceAttributeScopeString.SPAN:
        return Single.just(Optional.of(HypertraceAttributeScope.SPAN));
      case HypertraceAttributeScopeString.INTERACTION:
        return Single.just(Optional.of(HypertraceAttributeScope.INTERACTION));
      case HypertraceAttributeScopeString.API:
        return Single.just(Optional.of(HypertraceAttributeScope.API));
      case HypertraceAttributeScopeString.SERVICE:
        return Single.just(Optional.of(HypertraceAttributeScope.SERVICE));
      case HypertraceAttributeScopeString.API_TRACE:
        return Single.just(Optional.of(HypertraceAttributeScope.API_TRACE));
      case HypertraceAttributeScopeString.BACKEND:
        return Single.just(Optional.of(HypertraceAttributeScope.BACKEND));
      case HypertraceAttributeScopeString.BACKEND_TRACE:
        return Single.just(Optional.of(HypertraceAttributeScope.BACKEND_TRACE));
      default:
        return Single.just(Optional.empty());
    }
  }
}
