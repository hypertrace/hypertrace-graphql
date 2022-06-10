package org.hypertrace.core.graphql.context;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Injector;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AllArgsConstructor;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;

@Singleton
class AsyncDataFetcherFactory {

  private final Injector injector;
  private final GraphQlServiceConfig config;
  private final ExecutorService requestExecutor;

  @Inject
  AsyncDataFetcherFactory(Injector injector, GraphQlServiceConfig config) {
    this.injector = injector;
    this.config = config;
    this.requestExecutor =
        Executors.newFixedThreadPool(
            config.getMaxRequestThreads(),
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("request-handler-%d").build());
  }

  <T> DataFetcher<CompletableFuture<T>> buildDataFetcher(
      Class<? extends DataFetcher<CompletableFuture<T>>> dataFetcherClass) {
    return new AsyncForwardingDataFetcher<>(
        this.injector.getInstance(dataFetcherClass), requestExecutor, config);
  }

  @AllArgsConstructor
  private static class AsyncForwardingDataFetcher<T> implements DataFetcher<CompletableFuture<T>> {
    private final DataFetcher<CompletableFuture<T>> delegate;
    private final ExecutorService executorService;
    private final GraphQlServiceConfig config;

    @Override
    public CompletableFuture<T> get(DataFetchingEnvironment dataFetchingEnvironment)
        throws Exception {
      // Really all we're doing here is changing the thread that the future is run on by default
      return CompletableFuture.supplyAsync(
          () -> {
            try {
              return delegate
                  .get(dataFetchingEnvironment)
                  .get(config.getGraphQlTimeout().toMillis(), MILLISECONDS);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          },
          executorService);
    }
  }
}
