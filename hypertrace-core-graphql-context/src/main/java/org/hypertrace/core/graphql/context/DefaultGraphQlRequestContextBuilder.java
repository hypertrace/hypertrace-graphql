package org.hypertrace.core.graphql.context;

import com.google.common.collect.Streams;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContext;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContextBuilder;
import graphql.kickstart.servlet.context.GraphQLServletContext;
import graphql.schema.DataFetcher;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dataloader.DataLoaderRegistry;
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
    return new DefaultGraphQlRequestContext(request, response);
  }

  private final class DefaultGraphQlRequestContext implements GraphQlRequestContext {
    private final GraphQLServletContext servletContext;
    private final ContextualCachingKey cachingKey;

    private DefaultGraphQlRequestContext(HttpServletRequest request, HttpServletResponse response) {
      this.servletContext =
          DefaultGraphQLServletContext.createServletContext().with(request).with(response).build();
      this.cachingKey =
          new DefaultContextualCacheKey(this, this.getTenantId().orElse(DEFAULT_CONTEXT_ID));
    }

    @Override
    public Optional<Subject> getSubject() {
      return this.servletContext.getSubject();
    }

    @Override
    public Optional<DataLoaderRegistry> getDataLoaderRegistry() {
      return this.servletContext.getDataLoaderRegistry();
    }

    @Override
    public <T> DataFetcher<CompletableFuture<T>> constructDataFetcher(
        Class<? extends DataFetcher<CompletableFuture<T>>> dataFetcherClass) {
      return DefaultGraphQlRequestContextBuilder.this.dataFetcherFactory.buildDataFetcher(
          dataFetcherClass);
    }

    @Override
    public Optional<String> getAuthorizationHeader() {
      HttpServletRequest request = this.servletContext.getHttpServletRequest();
      return Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER_KEY))
          .or(() -> Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER_KEY.toLowerCase())));
    }

    @Override
    public Optional<String> getTenantId() {
      HttpServletRequest request = this.servletContext.getHttpServletRequest();
      return Optional.ofNullable(request.getHeader(TENANT_ID_HEADER_KEY))
          .or(DefaultGraphQlRequestContextBuilder.this.serviceConfig::getDefaultTenantId);
    }

    @Override
    public Map<String, String> getTracingContextHeaders() {
      return Streams.stream(
              this.servletContext.getHttpServletRequest().getHeaderNames().asIterator())
          .filter(
              header ->
                  TRACING_CONTEXT_HEADER_KEY_PREFIXES.stream()
                      .anyMatch(prefix -> header.toLowerCase().startsWith(prefix.toLowerCase())))
          .collect(
              Collectors.toUnmodifiableMap(
                  String::toLowerCase, this.servletContext.getHttpServletRequest()::getHeader));
    }

    @Nonnull
    @Override
    public ContextualCachingKey getCachingKey() {
      return this.cachingKey;
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
