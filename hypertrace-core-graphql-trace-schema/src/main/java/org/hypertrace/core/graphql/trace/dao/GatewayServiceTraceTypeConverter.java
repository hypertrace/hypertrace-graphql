package org.hypertrace.core.graphql.trace.dao;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.trace.schema.arguments.TraceType;

class GatewayServiceTraceTypeConverter implements Converter<TraceType, String> {

  private static final String WELL_KNOWN_PLATFORM_STRING_TRACE = "TRACE";
  private static final String WELL_KNOWN_PLATFORM_STRING_API_TRACE = "API_TRACE";
  private static final String WELL_KNOWN_PLATFORM_STRING_BACKEND_TRACE = "BACKEND_TRACE";

  @Override
  public Single<String> convert(TraceType explorerContext) {
    switch (explorerContext) {
      case TRACE:
        return Single.just(WELL_KNOWN_PLATFORM_STRING_TRACE);
      case API_TRACE:
        return Single.just(WELL_KNOWN_PLATFORM_STRING_API_TRACE);
      case BACKEND_TRACE:
        return Single.just(WELL_KNOWN_PLATFORM_STRING_BACKEND_TRACE);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format(
                    "Unable to convert unknown trace type '%s'", explorerContext.name())));
    }
  }
}
