package org.hypertrace.graphql.atttribute.scopes;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.utils.Converter;

class HypertraceAttributeScopeConverter implements Converter<AttributeModelScope, AttributeScope> {

  @Override
  public Single<AttributeScope> convert(AttributeModelScope scope) {
    switch (scope) {
      case TRACE:
        return Single.just(HypertraceAttributeScope.TRACE);
      case SPAN:
        return Single.just(HypertraceAttributeScope.SPAN);
      case INTERACTION:
        return Single.just(HypertraceAttributeScope.INTERACTION);
      case API:
        return Single.just(HypertraceAttributeScope.API);
      case SERVICE:
        return Single.just(HypertraceAttributeScope.SERVICE);
      case API_TRACE:
        return Single.just(HypertraceAttributeScope.API_TRACE);
      case BACKEND:
        return Single.just(HypertraceAttributeScope.BACKEND);
      case BACKEND_TRACE:
        return Single.just(HypertraceAttributeScope.BACKEND_TRACE);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format("Unrecognized attribute scope %s", scope.name())));
    }
  }
}
