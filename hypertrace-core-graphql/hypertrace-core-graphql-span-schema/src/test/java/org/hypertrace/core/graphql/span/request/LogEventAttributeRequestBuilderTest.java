package org.hypertrace.core.graphql.span.request;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Observable;
import java.util.Set;
import java.util.stream.Stream;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogEventAttributeRequestBuilderTest {

  @Mock GraphQlRequestContext mockContext;
  @Mock GraphQlSelectionFinder mockSelectionFinder;
  @Mock AttributeRequestBuilder mockAttributeRequestBuilder;
  @Mock DataFetchingFieldSelectionSet mockSelectionSet;
  @Mock Stream<SelectedField> mockAttributeQueryableStream;
  @Mock AttributeRequest mockFooAttributeRequest;
  @Mock AttributeRequest mockBarAttributeRequest;

  private LogEventAttributeRequestBuilder requestBuilder;

  @BeforeEach
  void beforeEach() {
    this.requestBuilder =
        new LogEventAttributeRequestBuilder(
            this.mockSelectionFinder, this.mockAttributeRequestBuilder);
  }

  @Test
  void canBuildRequest() {
    when(this.mockSelectionFinder.findSelections(eq(this.mockSelectionSet), any()))
        .thenReturn(mockAttributeQueryableStream);
    when(this.mockAttributeRequestBuilder.buildForAttributeQueryableFields(
            any(), any(), eq(this.mockAttributeQueryableStream)))
        .thenReturn(Observable.just(this.mockFooAttributeRequest, this.mockBarAttributeRequest));

    Set<AttributeRequest> request =
        this.requestBuilder
            .buildAttributeRequest(this.mockContext, this.mockSelectionSet)
            .blockingGet();

    Assertions.assertEquals(
        Set.of(this.mockBarAttributeRequest, this.mockFooAttributeRequest), request);
  }
}
