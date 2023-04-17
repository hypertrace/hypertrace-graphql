package org.hypertrace.core.graphql.context;

import com.google.common.collect.Streams;
import graphql.kickstart.execution.context.DefaultGraphQLContext;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContextBuilder;
import graphql.schema.DataFetcher;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;

class DefaultGraphQlRequestContextBuilder extends DefaultGraphQLServletContextBuilder
    implements GraphQlRequestContextBuilder {
  private static final String DEFAULT_CONTEXT_ID = "DEFAULT_CONTEXT_ID";
  static final String AUTHORIZATION_HEADER_KEY = "Authorization";
  static final String TENANT_ID_HEADER_KEY = "x-tenant-id";
  static final Set<String> TRACING_CONTEXT_HEADER_KEY_PREFIXES =
      Set.of("X-B3-", "traceparent", "tracestate");

  private final GraphQlServiceConfig serviceConfig;
  private final AsyncDataFetcherFactory dataFetcherFactory;

  @Inject
  DefaultGraphQlRequestContextBuilder(
      AsyncDataFetcherFactory dataFetcherFactory, GraphQlServiceConfig serviceConfig) {
    this.dataFetcherFactory = dataFetcherFactory;
    this.serviceConfig = serviceConfig;
  }

  @Override
  public GraphQlRequestContext build(HttpServletRequest request, HttpServletResponse response) {
    return new DefaultGraphQlRequestContext(request);
  }

  private final class DefaultGraphQlRequestContext extends DefaultGraphQLContext
      implements GraphQlRequestContext {
    private final ContextualCachingKey cachingKey;

    private final String requestId = UUID.randomUUID().toString();
    private final HttpServletRequest request;

    private DefaultGraphQlRequestContext(HttpServletRequest request) {
      this.request = request;
      this.cachingKey =
          new DefaultContextualCacheKey(this, this.getTenantId().orElse(DEFAULT_CONTEXT_ID));
      this.put(GraphQlRequestContext.class, this);
    }

    @Override
    public <T> DataFetcher<CompletableFuture<T>> constructDataFetcher(
        Class<? extends DataFetcher<CompletableFuture<T>>> dataFetcherClass) {
      return DefaultGraphQlRequestContextBuilder.this.dataFetcherFactory.buildDataFetcher(
          dataFetcherClass);
    }

    @Override
    public Optional<String> getAuthorizationHeader() {
      return Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER_KEY))
          .or(() -> Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER_KEY.toLowerCase())));
    }

    @Override
    public Optional<String> getTenantId() {
      return Optional.ofNullable(request.getHeader(TENANT_ID_HEADER_KEY))
          .or(DefaultGraphQlRequestContextBuilder.this.serviceConfig::getDefaultTenantId);
    }

    @Override
    public Map<String, String> getTracingContextHeaders() {
      return Streams.stream(request.getHeaderNames().asIterator())
          .filter(
              header ->
                  TRACING_CONTEXT_HEADER_KEY_PREFIXES.stream()
                      .anyMatch(prefix -> header.toLowerCase().startsWith(prefix.toLowerCase())))
          .collect(Collectors.toUnmodifiableMap(String::toLowerCase, request::getHeader));
    }

    @Nonnull
    @Override
    public ContextualCachingKey getCachingKey() {
      return this.cachingKey;
    }

    @Nonnull
    @Override
    public String getRequestId() {
      return this.requestId;
    }
  }

  private static class DefaultContextualCacheKey implements ContextualCachingKey {

    private final GraphQlRequestContext context;
    private final Object[] cacheInputs;

    private DefaultContextualCacheKey(GraphQlRequestContext context, Object... cacheInputs) {
      this.context = context;
      this.cacheInputs = cacheInputs;
    }

    @Override
    public GraphQlRequestContext getContext() {
      return this.context;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      DefaultContextualCacheKey that = (DefaultContextualCacheKey) o;
      return Arrays.equals(cacheInputs, that.cacheInputs);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(cacheInputs);
    }
  }
}
