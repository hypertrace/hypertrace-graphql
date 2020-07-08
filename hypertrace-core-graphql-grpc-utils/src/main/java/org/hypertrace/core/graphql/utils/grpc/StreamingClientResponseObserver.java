package org.hypertrace.core.graphql.utils.grpc;

import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import io.reactivex.rxjava3.core.ObservableEmitter;

class StreamingClientResponseObserver<ReqT, RespT> implements ClientResponseObserver<ReqT, RespT> {

  private final ObservableEmitter<RespT> emitter;
  private ClientCallStreamObserver<ReqT> requestStream;

  StreamingClientResponseObserver(final ObservableEmitter<RespT> emitter) {
    this.emitter = emitter;
    this.emitter.setCancellable(
        () ->
            this.requestStream.cancel(
                "StreamingClientResponseObserver cancelling after emitter disposed", null));
  }

  @Override
  public void beforeStart(ClientCallStreamObserver<ReqT> requestStream) {
    this.requestStream = requestStream;
  }

  @Override
  public void onNext(RespT value) {
    if (!this.emitter.isDisposed()) {
      this.emitter.onNext(value);
    }
  }

  @Override
  public void onError(Throwable t) {
    if (!this.emitter.isDisposed()) {
      this.emitter.onError(t);
    }
  }

  @Override
  public void onCompleted() {
    // This shouldn't generally happen - either an error or next response
    if (!this.emitter.isDisposed()) {
      emitter.onComplete();
    }
  }
}
