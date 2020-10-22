package org.hypertrace.core.graphql.atttributes.scopes;

import io.reactivex.rxjava3.core.Single;
import java.util.Optional;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.utils.Converter;

class HypertraceCoreAttributeScopeConverter implements Converter<String, Optional<AttributeScope>> {

  @Override
  public Single<Optional<AttributeScope>> convert(String scope) {
    switch (scope) {
      case HypertraceCoreAttributeScopeString.TRACE:
        return Single.just(Optional.of(HypertraceCoreAttributeScope.TRACE));
      case HypertraceCoreAttributeScopeString.SPAN:
        return Single.just(Optional.of(HypertraceCoreAttributeScope.SPAN));
      default:
        return Single.just(Optional.empty());
    }
  }
}
