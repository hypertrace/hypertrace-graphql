package org.hypertrace.graphql.atttribute.scopes;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.utils.Converter;

class HypertraceAttributeModelScopeConverter
    implements Converter<AttributeScope, AttributeModelScope> {

  @Override
  public Single<AttributeModelScope> convert(AttributeScope scope) {
    switch ((HypertraceAttributeScope) scope) {
      case TRACE:
        return Single.just(AttributeModelScope.TRACE);
      case SPAN:
        return Single.just(AttributeModelScope.SPAN);
      case API:
        return Single.just(AttributeModelScope.API);
      case INTERACTION:
        return Single.just(AttributeModelScope.INTERACTION);
      case SERVICE:
        return Single.just(AttributeModelScope.SERVICE);
      case API_TRACE:
        return Single.just(AttributeModelScope.API_TRACE);
      case BACKEND:
        return Single.just(AttributeModelScope.BACKEND);
      case BACKEND_TRACE:
        return Single.just(AttributeModelScope.BACKEND_TRACE);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format("Unrecognized attribute scope %s", scope)));
    }
  }
}
