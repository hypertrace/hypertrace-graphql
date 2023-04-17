package org.hypertrace.core.graphql.span.fetcher;

import static org.hypertrace.core.graphql.context.GraphQlRequestContext.contextFromEnvironment;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.reactivex.rxjava3.core.Single;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.core.graphql.span.dao.ExportSpanDao;
import org.hypertrace.core.graphql.span.export.ExportSpanConstants.LogEventAttributes;
import org.hypertrace.core.graphql.span.export.ExportSpanConstants.SpanAttributes;
import org.hypertrace.core.graphql.span.request.SpanRequest;
import org.hypertrace.core.graphql.span.request.SpanRequestBuilder;
import org.hypertrace.core.graphql.span.schema.ExportSpanResult;

public class ExportSpanFetcher extends InjectableDataFetcher<ExportSpanResult> {

  public ExportSpanFetcher() {
    super(ExportSpanFetcherImpl.class);
  }

  static final class ExportSpanFetcherImpl
      implements DataFetcher<CompletableFuture<ExportSpanResult>> {
    private final SpanRequestBuilder requestBuilder;
    private final ExportSpanDao exportSpanDao;

    @Inject
    ExportSpanFetcherImpl(SpanRequestBuilder requestBuilder, ExportSpanDao exportSpanDao) {
      this.requestBuilder = requestBuilder;
      this.exportSpanDao = exportSpanDao;
    }

    @Override
    public CompletableFuture<ExportSpanResult> get(DataFetchingEnvironment environment) {
      Single<SpanRequest> spanRequest =
          this.requestBuilder.build(
              contextFromEnvironment(environment),
              environment.getArguments(),
              SpanAttributes.SPAN_ATTRIBUTES,
              LogEventAttributes.LOG_EVENT_ATTRIBUTES);

      return spanRequest
          .flatMap(this.exportSpanDao::getSpans)
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
