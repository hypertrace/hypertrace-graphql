package org.hypertrace.core.graphql.context;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsyncDataFetcherFactoryTest {
  @Mock GraphQlServiceConfig graphQlServiceConfig;
  @Mock DataFetchingEnvironment dataFetchingEnvironment;

  @Test
  void canBuildAsyncDataFetcher() throws Exception {
    when(graphQlServiceConfig.getMaxRequestThreads()).thenReturn(1);
    DataFetcher<CompletableFuture<Thread>> fetcher =
        new AsyncDataFetcherFactory(Guice.createInjector(), graphQlServiceConfig)
            .buildDataFetcher(ThreadEchoingDataFetcher.class);

    Thread fetcherThread = fetcher.get(dataFetchingEnvironment).get();

    assertNotEquals(Thread.currentThread(), fetcherThread);
    assertNotNull(fetcherThread);
  }

  private static class ThreadEchoingDataFetcher implements DataFetcher<CompletableFuture<Thread>> {
    @Override
    public CompletableFuture<Thread> get(DataFetchingEnvironment environment) {
      return CompletableFuture.completedFuture(Thread.currentThread());
    }
  }
}
