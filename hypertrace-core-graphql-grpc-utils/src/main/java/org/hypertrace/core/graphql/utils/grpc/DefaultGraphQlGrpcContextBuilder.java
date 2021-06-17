package org.hypertrace.core.graphql.utils.grpc;

import io.grpc.stub.StreamObserver;
import io.reactivex.rxjava3.core.Observable;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.inject.Inject;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.grpcutils.context.RequestContext;

class DefaultGraphQlGrpcContextBuilder implements GraphQlGrpcContextBuilder {

  private final PlatformGrpcContextBuilder platformGrpcContextBuilder;

  @Inject
  DefaultGraphQlGrpcContextBuilder(PlatformGrpcContextBuilder platformGrpcContextBuilder) {
    this.platformGrpcContextBuilder = platformGrpcContextBuilder;
  }

  @Override
  public GraphQlGrpcContext build(GraphQlRequestContext requestContext) {
    return new DefaultGraphQlGrpcContext(requestContext, this.platformGrpcContextBuilder);
  }

  private static final class DefaultGraphQlGrpcContext implements GraphQlGrpcContext {

    private final RequestContext grpcContext;

    private DefaultGraphQlGrpcContext(
        GraphQlRequestContext requestContext,
        PlatformGrpcContextBuilder platformGrpcContextBuilder) {
      this.grpcContext = platformGrpcContextBuilder.build(requestContext);
    }

    @Override
    public <TResp> TResp callInContext(Callable<TResp> callable) {
      return this.grpcContext.call(callable);
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
  }
}
