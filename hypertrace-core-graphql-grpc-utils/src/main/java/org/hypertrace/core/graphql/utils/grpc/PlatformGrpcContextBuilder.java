package org.hypertrace.core.graphql.utils.grpc;

import static org.hypertrace.core.grpcutils.context.RequestContextConstants.AUTHORIZATION_HEADER;
import static org.hypertrace.core.grpcutils.context.RequestContextConstants.REQUEST_ID_HEADER_KEY;
import static org.hypertrace.core.grpcutils.context.RequestContextConstants.TENANT_ID_HEADER_KEY;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.grpcutils.context.RequestContext;

class PlatformGrpcContextBuilder implements GrpcContextBuilder {

  private final Cache<String, GraphQlRequestContext> contextCache =
      CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(1)).maximumSize(1000).build();

  @Override
  public RequestContext build(GraphQlRequestContext requestContext) {
    this.contextCache.put(requestContext.getRequestId(), requestContext);
    Map<String, String> grpcHeaders =
        this.mergeMaps(
            requestContext.getTracingContextHeaders(),
            this.flattenOptionalMap(
                Map.of(
                    AUTHORIZATION_HEADER, this.extractAuthorizationHeader(requestContext),
                    TENANT_ID_HEADER_KEY, this.extractTenantId(requestContext),
                    REQUEST_ID_HEADER_KEY, Optional.of(requestContext.getRequestId()))));

    return this.build(grpcHeaders);
  }

  @Override
  public Optional<GraphQlRequestContext> tryRestore(RequestContext requestContext) {
    return requestContext.getRequestId().map(this.contextCache::getIfPresent);
  }

  private RequestContext build(@Nonnull Map<String, String> headers) {
    RequestContext platformContext = new RequestContext();
    headers.forEach(platformContext::add);
    return platformContext;
  }

  private Optional<String> extractAuthorizationHeader(GraphQlRequestContext requestContext) {
    return requestContext.getAuthorizationHeader().filter(header -> header.length() > 0);
  }

  private Optional<String> extractTenantId(GraphQlRequestContext requestContext) {
    return requestContext.getTenantId();
  }

  private Map<String, String> flattenOptionalMap(Map<String, Optional<String>> optionalMap) {
    return optionalMap.entrySet().stream()
        .map(entry -> entry.getValue().map(value -> Map.entry(entry.getKey(), value)))
        .flatMap(Optional::stream)
        .collect(Collectors.toUnmodifiableMap(Entry::getKey, Entry::getValue));
  }

  @SafeVarargs
  private Map<String, String> mergeMaps(Map<String, String>... maps) {
    return Arrays.stream(maps)
        .map(Map::entrySet)
        .flatMap(Collection::stream)
        .collect(Collectors.toUnmodifiableMap(Entry::getKey, Entry::getValue));
  }
}
