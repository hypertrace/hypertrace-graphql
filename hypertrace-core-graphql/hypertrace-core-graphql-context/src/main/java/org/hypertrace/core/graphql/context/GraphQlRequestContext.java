package org.hypertrace.core.graphql.context;

import graphql.kickstart.execution.context.GraphQLKickstartContext;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public interface GraphQlRequestContext extends GraphQLKickstartContext {

  /**
   * A tool to create data fetchers via injection container due to limitations in the framework. For
   * normal injectable instantiation, do not use this method.
   */
  <T> DataFetcher<CompletableFuture<T>> constructDataFetcher(
      Class<? extends DataFetcher<CompletableFuture<T>>> dataFetcherClass);

  Optional<String> getAuthorizationHeader();

  Optional<String> getTenantId();

  Map<String, String> getTracingContextHeaders();

  @Nonnull
  ContextualCachingKey getCachingKey();

  String getRequestId();

  static GraphQlRequestContext contextFromEnvironment(DataFetchingEnvironment environment) {
    return environment.getGraphQlContext().get(GraphQlRequestContext.class);
  }
}
