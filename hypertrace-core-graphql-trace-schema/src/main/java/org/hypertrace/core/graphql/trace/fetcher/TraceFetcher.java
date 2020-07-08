package org.hypertrace.core.graphql.trace.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.core.graphql.trace.dao.TraceDao;
import org.hypertrace.core.graphql.trace.request.TraceRequestBuilder;
import org.hypertrace.core.graphql.trace.schema.TraceResultSet;

public class TraceFetcher extends InjectableDataFetcher<TraceResultSet> {

  public TraceFetcher() {
    super(TraceFetcherImpl.class);
  }

  static final class TraceFetcherImpl implements DataFetcher<CompletableFuture<TraceResultSet>> {
    private final TraceRequestBuilder requestBuilder;
    private final TraceDao traceDao;

    @Inject
    TraceFetcherImpl(TraceRequestBuilder requestBuilder, TraceDao traceDao) {
      this.requestBuilder = requestBuilder;
      this.traceDao = traceDao;
    }

    @Override
    public CompletableFuture<TraceResultSet> get(DataFetchingEnvironment environment) {
      return this.requestBuilder
          .build(
              environment.getContext(), environment.getArguments(), environment.getSelectionSet())
          .flatMap(this.traceDao::getTraces)
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
