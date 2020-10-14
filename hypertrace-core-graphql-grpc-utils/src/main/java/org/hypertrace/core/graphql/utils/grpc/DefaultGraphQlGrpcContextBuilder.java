package org.hypertrace.core.graphql.utils.grpc;

import static org.hypertrace.core.grpcutils.context.RequestContextConstants.AUTHORIZATION_HEADER;
import static org.hypertrace.core.grpcutils.context.RequestContextConstants.TENANT_ID_HEADER_KEY;

import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import io.reactivex.rxjava3.core.Observable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.grpcutils.context.RequestContext;

class DefaultGraphQlGrpcContextBuilder implements GraphQlGrpcContextBuilder {

  private final PlatformRequestContextBuilder platformRequestContextBuilder;

  @Inject
  DefaultGraphQlGrpcContextBuilder(PlatformRequestContextBuilder platformRequestContextBuilder) {
    this.platformRequestContextBuilder = platformRequestContextBuilder;
  }

  @Override
  public GraphQlGrpcContext build(GraphQlRequestContext requestContext) {
    return new DefaultGraphQlGrpcContext(requestContext, this.platformRequestContextBuilder);
  }

  private static final class DefaultGraphQlGrpcContext implements GraphQlGrpcContext {
    private final Context grpcContext;

    private DefaultGraphQlGrpcContext(
        GraphQlRequestContext requestContext,
        PlatformRequestContextBuilder platformRequestContextBuilder) {
      Map<String, String> grpcHeaders =
          this.mergeMaps(
              requestContext.getTracingContextHeaders(),
              this.flattenOptionalMap(
                  Map.of(
                      AUTHORIZATION_HEADER, this.extractAuthorizationHeader(requestContext),
                      TENANT_ID_HEADER_KEY, this.extractTenantId(requestContext))));

      RequestContext platformContext = platformRequestContextBuilder.build(grpcHeaders);
      this.grpcContext = this.buildGrpcContext(platformContext);
    }

    private Optional<String> extractAuthorizationHeader(GraphQlRequestContext requestContext) {
      return requestContext.getAuthorizationHeader().filter(header -> header.length() > 0);
    }

    private Optional<String> extractTenantId(GraphQlRequestContext requestContext) {
      return requestContext.getTenantId();
    }

    @Override
    public <TResp> TResp callInContext(Callable<TResp> callable) {
      try {
        return this.grpcContext.call(callable);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void runInContext(Runnable runnable) {
      this.grpcContext.run(runnable);
    }

    @Override
    public <TResponse> Observable<TResponse> streamInContext(
        Consumer<StreamObserver<TResponse>> requestExecutor) {
      return Observable.create(
          emitter ->
              this.runInContext(
                  () -> requestExecutor.accept(new StreamingClientResponseObserver<>(emitter))));
    }

    private Context buildGrpcContext(RequestContext platformContext) {
      return Context.current().withValue(RequestContext.CURRENT, platformContext);
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
}
