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
import org.hypertrace.core.graphql.spi.config.GraphQlEndpointConfig;

@Singleton
class AsyncDataFetcherFactory {

  private final Injector injector;
  private final GraphQlEndpointConfig endpointConfig;
  private final ExecutorService requestExecutor;

  @Inject
  AsyncDataFetcherFactory(Injector injector, GraphQlEndpointConfig endpointConfig) {
    this.injector = injector;
    this.endpointConfig = endpointConfig;
    this.requestExecutor =
        Executors.newFixedThreadPool(
            endpointConfig.getMaxRequestThreads(),
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("request-handler-%d").build());
  }

  <T> DataFetcher<CompletableFuture<T>> buildDataFetcher(
      Class<? extends DataFetcher<CompletableFuture<T>>> dataFetcherClass) {
    return new AsyncForwardingDataFetcher<>(
        this.injector.getInstance(dataFetcherClass), requestExecutor, endpointConfig);
  }

  @AllArgsConstructor
  private static class AsyncForwardingDataFetcher<T> implements DataFetcher<CompletableFuture<T>> {
    private final DataFetcher<CompletableFuture<T>> delegate;
    private final ExecutorService executorService;
    private final GraphQlEndpointConfig config;

    @Override
    public CompletableFuture<T> get(DataFetchingEnvironment dataFetchingEnvironment)
        throws Exception {
      // Really all we're doing here is changing the thread that the future is run on by default
      return CompletableFuture.supplyAsync(
          () -> {
            try {
              return delegate
                  .get(dataFetchingEnvironment)
                  .get(config.getTimeout().toMillis(), MILLISECONDS);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          },
          executorService);
    }
  }
}
