package org.hypertrace.core.graphql.common.fetcher;

import static org.hypertrace.core.graphql.context.GraphQlRequestContext.contextFromEnvironment;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public abstract class InjectableDataFetcher<T> implements DataFetcher<CompletableFuture<T>> {

  private final Class<? extends DataFetcher<CompletableFuture<T>>> dataFetcherClass;
  @Nullable private DataFetcher<CompletableFuture<T>> dataFetcherInstance;

  protected InjectableDataFetcher(
      Class<? extends DataFetcher<CompletableFuture<T>>> dataFetcherClass) {
    this.dataFetcherClass = dataFetcherClass;
  }

  @Override
  public final CompletableFuture<T> get(DataFetchingEnvironment environment) throws Exception {
    return this.getOrCreateImplementation(contextFromEnvironment(environment)).get(environment);
  }

  private DataFetcher<CompletableFuture<T>> getOrCreateImplementation(
      GraphQlRequestContext context) {
    if (this.dataFetcherInstance == null) {
      this.dataFetcherInstance = context.constructDataFetcher(this.dataFetcherClass);
    }

    return this.dataFetcherInstance;
  }
}
