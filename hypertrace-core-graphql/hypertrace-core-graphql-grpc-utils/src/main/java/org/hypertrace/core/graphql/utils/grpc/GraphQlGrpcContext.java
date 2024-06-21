package org.hypertrace.core.graphql.utils.grpc;

import io.grpc.stub.StreamObserver;
import io.reactivex.rxjava3.core.Observable;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

@Deprecated
public interface GraphQlGrpcContext {

  <TResp> TResp callInContext(Callable<TResp> callable);

  void runInContext(Runnable runnable);

  <TResponse> Observable<TResponse> streamInContext(
      Consumer<StreamObserver<TResponse>> requestExecutor);
}
