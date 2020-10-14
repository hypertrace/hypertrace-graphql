package org.hypertrace.core.graphql.context;

import graphql.kickstart.execution.context.GraphQLContext;
import graphql.schema.DataFetcher;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface GraphQlRequestContext extends GraphQLContext {

  /**
   * A tool to create data fetchers via injection container due to limitations in the framework. For
   * normal injectable instantiation, do not use this method.
   */
  <T extends DataFetcher<?>> T constructDataFetcher(Class<T> dataFetcherClass);

  Optional<String> getAuthorizationHeader();

  Optional<String> getTenantId();

  Map<String, String> getTracingContextHeaders();

  @Nonnull
  ContextualCachingKey getCachingKey();
}
