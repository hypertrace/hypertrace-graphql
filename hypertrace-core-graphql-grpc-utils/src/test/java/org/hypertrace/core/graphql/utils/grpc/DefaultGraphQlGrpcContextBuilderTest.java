package org.hypertrace.core.graphql.utils.grpc;

import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.observers.TestObserver;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.grpcutils.context.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultGraphQlGrpcContextBuilderTest {
  @Mock PlatformGrpcContextBuilder mockPlatformGrpcContextBuilder;
  @Mock GraphQlRequestContext mockRequestContext;

  @Mock(answer = Answers.CALLS_REAL_METHODS)
  RequestContext mockPlatformContext;

  private GraphQlGrpcContextBuilder builder;

  @BeforeEach
  void beforeEach() {
    this.builder = new DefaultGraphQlGrpcContextBuilder(this.mockPlatformGrpcContextBuilder);
    when(this.mockPlatformGrpcContextBuilder.build(this.mockRequestContext))
        .thenReturn(this.mockPlatformContext);
  }

  @Test
  void addsGrpcContextToStreamedRequest() {
    var context = this.builder.build(this.mockRequestContext);
    var testObserver = new TestObserver<>();
    context
        .streamInContext(
            streamObserver -> {
              streamObserver.onNext(RequestContext.CURRENT.get());
              streamObserver.onCompleted();
            })
        .subscribe(testObserver);

    testObserver.assertValue(this.mockPlatformContext);
    testObserver.assertComplete();
  }

  @Test
  void propagatesErrorsFromStreamedRequest() {
    var context = this.builder.build(this.mockRequestContext);
    var testObserver = new TestObserver<>();
    context
        .streamInContext(streamObserver -> streamObserver.onError(new IllegalStateException()))
        .subscribe(testObserver);

    testObserver.assertError(IllegalStateException.class);
  }
}
