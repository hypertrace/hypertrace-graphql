package org.hypertrace.graphql.explorer.context;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.graphql.explorer.schema.argument.ExplorerContext;

class HypertraceExploreContextStringConverter implements Converter<ExplorerContext, String> {
  private static final String WELL_KNOWN_STRING_TRACE = "TRACE";
  private static final String WELL_KNOWN_STRING_API = "API";
  private static final String WELL_KNOWN_STRING_SERVICE = "SERVICE";
  private static final String WELL_KNOWN_STRING_API_TRACE = "API_TRACE";
  private static final String WELL_KNOWN_STRING_BACKEND_TRACE = "BACKEND_TRACE";
  private static final String WELL_KNOWN_STRING_SPAN = "EVENT";

  @Override
  public Single<String> convert(ExplorerContext explorerContext) {
    switch ((HypertraceExplorerContext) explorerContext) {
      case TRACE:
        return Single.just(WELL_KNOWN_STRING_TRACE);
      case API:
        return Single.just(WELL_KNOWN_STRING_API);
      case SERVICE:
        return Single.just(WELL_KNOWN_STRING_SERVICE);
      case API_TRACE:
        return Single.just(WELL_KNOWN_STRING_API_TRACE);
      case BACKEND_TRACE:
        return Single.just(WELL_KNOWN_STRING_BACKEND_TRACE);
      case SPAN:
        return Single.just(WELL_KNOWN_STRING_SPAN);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format("Unable to convert unknown explore context '%s'", explorerContext)));
    }
  }
}
