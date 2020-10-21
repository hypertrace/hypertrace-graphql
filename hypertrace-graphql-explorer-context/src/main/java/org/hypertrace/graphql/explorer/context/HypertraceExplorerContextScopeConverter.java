package org.hypertrace.graphql.explorer.context;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.graphql.explorer.schema.argument.ExplorerContext;

class HypertraceExplorerContextScopeConverter
    implements Converter<ExplorerContext, AttributeModelScope> {

  @Override
  public Single<AttributeModelScope> convert(ExplorerContext explorerContext) {
    switch ((HypertraceExplorerContext) explorerContext) {
      case TRACE:
        return Single.just(AttributeModelScope.TRACE);
      case API:
        return Single.just(AttributeModelScope.API);
      case SERVICE:
        return Single.just(AttributeModelScope.SERVICE);
      case BACKEND:
        return Single.just(AttributeModelScope.BACKEND);
      case API_TRACE:
        return Single.just(AttributeModelScope.API_TRACE);
      case BACKEND_TRACE:
        return Single.just(AttributeModelScope.BACKEND_TRACE);
      case SPAN:
        return Single.just(AttributeModelScope.SPAN);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format("Unable to convert unknown explore context '%s'", explorerContext)));
    }
  }
}
