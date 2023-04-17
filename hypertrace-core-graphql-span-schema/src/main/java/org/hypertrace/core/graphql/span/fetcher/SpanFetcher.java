package org.hypertrace.core.graphql.span.fetcher;

import static org.hypertrace.core.graphql.context.GraphQlRequestContext.contextFromEnvironment;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.core.graphql.span.dao.SpanDao;
import org.hypertrace.core.graphql.span.request.SpanRequestBuilder;
import org.hypertrace.core.graphql.span.schema.SpanResultSet;

public class SpanFetcher extends InjectableDataFetcher<SpanResultSet> {

  public SpanFetcher() {
    super(SpanFetcherImpl.class);
  }

  static final class SpanFetcherImpl implements DataFetcher<CompletableFuture<SpanResultSet>> {
    private final SpanRequestBuilder requestBuilder;
    private final SpanDao spanDao;

    @Inject
    SpanFetcherImpl(SpanRequestBuilder requestBuilder, SpanDao spanDao) {
      this.requestBuilder = requestBuilder;
      this.spanDao = spanDao;
    }

    @Override
    public CompletableFuture<SpanResultSet> get(DataFetchingEnvironment environment) {
      return this.requestBuilder
          .build(
              contextFromEnvironment(environment),
              environment.getArguments(),
              environment.getSelectionSet())
          .flatMap(this.spanDao::getSpans)
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
