package org.hypertrace.core.graphql.request.transformation;

import static org.mockito.Mockito.mock;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;
import java.util.Set;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.junit.jupiter.api.Test;

class RequestTransformerImplTest {

  @Test
  void noApplicableTransformerReturnsInput() {
    RequestTransformer transformer = new RequestTransformerImpl(Set.of());

    ContextualRequest request = mock(ContextualRequest.class);

    TestObserver<ContextualRequest> testObserver = new TestObserver<>();
    transformer.transform(request).subscribe(testObserver);

    testObserver.assertResult(request);
  }

  @Test
  void skipsUnsupportedTransformers() {
    GraphQlRequestContext context = mock(GraphQlRequestContext.class);
    RequestTransformer transformer =
        new RequestTransformerImpl(
            Set.of(
                new TestTransformation(true, "prefix-", ""),
                new TestTransformation(false, "", "-suffix")));

    TestObserver<ContextualRequest> testObserver = new TestObserver<>();
    transformer.transform(new TestContextualRequest(context, "original")).subscribe(testObserver);

    testObserver.assertResult(new TestContextualRequest(context, "prefix-original"));
  }

  @Test
  void appliesAllTransformers() {
    GraphQlRequestContext context = mock(GraphQlRequestContext.class);
    RequestTransformer transformer =
        new RequestTransformerImpl(
            Set.of(
                new TestTransformation(true, "prefix-", ""),
                new TestTransformation(true, "", "-suffix")));

    TestObserver<ContextualRequest> testObserver = new TestObserver<>();
    transformer.transform(new TestContextualRequest(context, "original")).subscribe(testObserver);

    testObserver.assertResult(new TestContextualRequest(context, "prefix-original-suffix"));
  }

  @Value
  private static class TestTransformation implements RequestTransformation {
    boolean supported;
    String prefixToAdd;
    String suffixToAdd;

    @Override
    public boolean supportsRequest(ContextualRequest request) {
      return supported;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ContextualRequest> Single<T> transform(T request) {
      String previousValue = ((TestContextualRequest) request).value();
      TestContextualRequest newRequest =
          new TestContextualRequest(request.context(), prefixToAdd + previousValue + suffixToAdd);
      return Single.just((T) newRequest);
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class TestContextualRequest implements ContextualRequest {
    GraphQlRequestContext context;
    String value;
  }
}
