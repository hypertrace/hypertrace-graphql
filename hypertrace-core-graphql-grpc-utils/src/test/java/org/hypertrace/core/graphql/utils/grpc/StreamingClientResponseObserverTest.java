package org.hypertrace.core.graphql.utils.grpc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import io.grpc.stub.ClientCallStreamObserver;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StreamingClientResponseObserverTest {

  @Mock ClientCallStreamObserver<String> requestStreamObserver;

  private StreamingClientResponseObserver<String, String> responseObserver;
  private TestObserver<String> testObserver;

  @BeforeEach
  void beforeEach() {
    Observable<String> observable =
        Observable.create(
            observer -> this.responseObserver = new StreamingClientResponseObserver<>(observer));
    this.testObserver = new TestObserver<>();
    observable.subscribe(this.testObserver);
    this.responseObserver.beforeStart(this.requestStreamObserver);
  }

  @Test
  void returnsValues() {
    this.responseObserver.onNext("first");
    this.testObserver.assertValue("first");

    this.responseObserver.onNext("second");
    this.testObserver.assertValueAt(1, "second");
    this.testObserver.assertNotComplete();

    verifyNoInteractions(requestStreamObserver);
  }

  @Test
  void propagatesExceptionOnError() {
    Throwable t = new Exception("error");
    this.responseObserver.onError(t);

    this.testObserver.assertError(t);
    this.testObserver.assertNoValues();
    verify(requestStreamObserver).cancel(any(), any());
  }

  @Test
  void propagatesCompletion() {
    this.responseObserver.onCompleted();

    this.testObserver.assertComplete();
    this.testObserver.assertNoValues();

    verify(requestStreamObserver).cancel(any(), any());
  }
}
