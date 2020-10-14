package org.hypertrace.core.graphql.utils.grpc;

import static org.hypertrace.core.grpcutils.context.RequestContextConstants.TENANT_ID_HEADER_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.observers.TestObserver;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.grpcutils.context.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultGraphQlGrpcContextBuilderTest {

  private static final Optional<String> TEST_TENANT_ID_OPTIONAL = Optional.of("testTenant");
  @Mock PlatformRequestContextBuilder mockPlatformRequestContextBuilder;
  @Mock GraphQlRequestContext mockRequestContext;
  @Mock RequestContext mockPlatformContext;

  private GraphQlGrpcContextBuilder builder;

  @BeforeEach
  void beforeEach() {
    this.builder = new DefaultGraphQlGrpcContextBuilder(this.mockPlatformRequestContextBuilder);
    when(this.mockPlatformRequestContextBuilder.build(anyMap()))
        .thenReturn(this.mockPlatformContext);
    when(this.mockRequestContext.getAuthorizationHeader()).thenReturn(Optional.empty());
  }

  @Test
  void passesAuthHeaderToPlatformContext() {
    this.builder.build(this.mockRequestContext);
    verify(this.mockPlatformRequestContextBuilder).build(Collections.emptyMap());
    when(this.mockRequestContext.getAuthorizationHeader()).thenReturn(Optional.of("auth header"));
    this.builder.build(this.mockRequestContext);
    verify(this.mockPlatformRequestContextBuilder).build(Map.of("authorization", "auth header"));
  }

  @Test
  void addsGrpcContextToRunnable() {
    when(this.mockPlatformContext.getTenantId()).thenReturn(TEST_TENANT_ID_OPTIONAL);
    var context = this.builder.build(this.mockRequestContext);
    var mockRunnable = mock(Runnable.class);

    doAnswer(
            invocation -> {
              assertEquals(TEST_TENANT_ID_OPTIONAL, RequestContext.CURRENT.get().getTenantId());
              return null;
            })
        .when(mockRunnable)
        .run();

    assertNull(RequestContext.CURRENT.get());
    context.runInContext(mockRunnable);

    verify(mockRunnable, times(1)).run();
  }

  @Test
  void propagatesRunnableError() {
    var context = this.builder.build(this.mockRequestContext);
    assertNull(RequestContext.CURRENT.get());
    assertThrows(
        RuntimeException.class,
        () ->
            context.callInContext(
                () -> {
                  throw new RuntimeException("test");
                }));
  }

  @Test
  void addsGrpcContextToStreamedRequest() {
    when(this.mockPlatformContext.getTenantId()).thenReturn(TEST_TENANT_ID_OPTIONAL);
    var context = this.builder.build(this.mockRequestContext);
    var testObserver = new TestObserver<>();
    context
        .streamInContext(
            streamObserver -> {
              streamObserver.onNext(RequestContext.CURRENT.get().getTenantId());
              streamObserver.onCompleted();
            })
        .subscribe(testObserver);

    testObserver.assertValue(TEST_TENANT_ID_OPTIONAL);
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

  @Test
  void addsGrpcContextToCallable() {
    when(this.mockPlatformContext.getTenantId()).thenReturn(TEST_TENANT_ID_OPTIONAL);
    var context = this.builder.build(this.mockRequestContext);
    assertNull(RequestContext.CURRENT.get());
    assertEquals(
        TEST_TENANT_ID_OPTIONAL,
        context.callInContext(() -> RequestContext.CURRENT.get().getTenantId()));
  }

  @Test
  void propagatesCallableError() {
    var context = this.builder.build(this.mockRequestContext);
    assertNull(RequestContext.CURRENT.get());
    assertThrows(
        RuntimeException.class,
        () ->
            context.callInContext(
                () -> {
                  throw new RuntimeException("test");
                }));
  }

  @Test
  void addsTenantIdToContext() {
    when(this.mockRequestContext.getTenantId()).thenReturn(Optional.of("tenant id"));
    this.builder.build(this.mockRequestContext);
    verify(this.mockPlatformRequestContextBuilder).build(Map.of(TENANT_ID_HEADER_KEY, "tenant id"));
  }

  @Test
  void addsTracingHeadersToContext() {
    when(this.mockRequestContext.getTenantId()).thenReturn(Optional.of("tenant id"));
    when(this.mockRequestContext.getTracingContextHeaders())
        .thenReturn(Map.of("traceid", "traceid value"));
    this.builder.build(this.mockRequestContext);
    verify(this.mockPlatformRequestContextBuilder)
        .build(Map.of(TENANT_ID_HEADER_KEY, "tenant id", "traceid", "traceid value"));
  }
}
