package org.hypertrace.core.graphql.trace.request;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.trace.schema.arguments.TraceType;

class TraceTypeToAttributeModelScopeConverter implements Converter<TraceType, AttributeModelScope> {

  @Override
  public Single<AttributeModelScope> convert(TraceType traceType) {
    switch (traceType) {
      case TRACE:
        return Single.just(AttributeModelScope.TRACE);
      case API_TRACE:
        return Single.just(AttributeModelScope.API_TRACE);
      case BACKEND_TRACE:
        return Single.just(AttributeModelScope.BACKEND_TRACE);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format("Unable to convert unknown trace type '%s'", traceType.name())));
    }
  }
}
