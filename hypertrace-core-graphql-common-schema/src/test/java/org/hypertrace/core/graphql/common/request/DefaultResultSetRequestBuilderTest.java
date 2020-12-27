package org.hypertrace.core.graphql.common.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.LimitArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.OffsetArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.space.SpaceArgument;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeAssociator;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultResultSetRequestBuilderTest {
  @Mock GraphQlRequestContext mockContext;
  @Mock ArgumentDeserializer mockArgumentDeserializer;
  @Mock GraphQlSelectionFinder mockSelectionFinder;
  @Mock AttributeRequestBuilder mockAttributeRequestBuilder;
  @Mock AttributeAssociator mockAttributeAssociator;
  @Mock FilterRequestBuilder mockFilterBuilder;
  @Mock TimeRangeArgument mockTimeRange;
  @Mock OrderArgument mockOrderArgument;
  @Mock FilterArgument mockFilterArgument;
  @Mock AttributeModel mockFooAttribute;
  int mockLimit = 3;
  int mockOffset = 4;
  String mockSpace = "mock-space";
  @Mock DataFetchingFieldSelectionSet mockSelectionSet;
  @Mock Stream<SelectedField> mockAttributeQueryableStream;
  @Mock AttributeRequest mockFooAttributeRequest;
  @Mock AttributeRequest mockIdAttributeRequest;

  private DefaultResultSetRequestBuilder requestBuilder;

  @BeforeEach
  void beforeEach() {
    this.requestBuilder =
        new DefaultResultSetRequestBuilder(
            this.mockArgumentDeserializer,
            this.mockSelectionFinder,
            this.mockAttributeRequestBuilder,
            this.mockAttributeAssociator,
            this.mockFilterBuilder);
  }

  @Test
  void canBuildRequest() {
    when(this.mockAttributeAssociator.associateAttributes(
            any(), eq("SPAN"), eq(List.of(this.mockOrderArgument)), any()))
        .thenReturn(
            Observable.just(
                AttributeAssociation.of(this.mockFooAttribute, this.mockOrderArgument)));
    when(this.mockFilterBuilder.build(
            any(), eq("SPAN"), eq(List.of(this.mockFilterArgument))))
        .thenReturn(
            Single.just(
                List.of(AttributeAssociation.of(this.mockFooAttribute, this.mockFilterArgument))));
    when(this.mockSelectionFinder.findSelections(eq(this.mockSelectionSet), any()))
        .thenReturn(mockAttributeQueryableStream);
    when(this.mockAttributeRequestBuilder.buildForAttributeQueryableFieldsAndId(
            any(), any(), eq(this.mockAttributeQueryableStream)))
        .thenReturn(Observable.just(this.mockFooAttributeRequest, this.mockIdAttributeRequest));
    when(this.mockAttributeRequestBuilder.buildForId(any(), any()))
        .thenReturn(Single.just(this.mockIdAttributeRequest));
    when(this.mockArgumentDeserializer.deserializePrimitive(any(), eq(LimitArgument.class)))
        .thenReturn(Optional.of(this.mockLimit));
    when(this.mockArgumentDeserializer.deserializePrimitive(any(), eq(OffsetArgument.class)))
        .thenReturn(Optional.of(this.mockOffset));
    when(this.mockArgumentDeserializer.deserializeObject(any(), eq(TimeRangeArgument.class)))
        .thenReturn(Optional.of(this.mockTimeRange));
    when(this.mockArgumentDeserializer.deserializeObjectList(any(), eq(OrderArgument.class)))
        .thenReturn(Optional.of(List.of(this.mockOrderArgument)));
    when(this.mockArgumentDeserializer.deserializeObjectList(any(), eq(FilterArgument.class)))
        .thenReturn(Optional.of(List.of(this.mockFilterArgument)));
    when(this.mockArgumentDeserializer.deserializePrimitive(any(), eq(SpaceArgument.class)))
        .thenReturn(Optional.of(this.mockSpace));

    ResultSetRequest<OrderArgument> request =
        this.requestBuilder
            .build(
                this.mockContext,
                "SPAN",
                Collections.emptyMap(), // Arg parser mocked, don't need values
                this.mockSelectionSet)
            .blockingGet();

    assertEquals(
        Set.of(this.mockIdAttributeRequest, this.mockFooAttributeRequest), request.attributes());
    assertEquals(this.mockLimit, request.limit());
    assertEquals(this.mockOffset, request.offset());
    assertEquals(this.mockTimeRange, request.timeRange());
    assertEquals(
        List.of(AttributeAssociation.of(this.mockFooAttribute, this.mockOrderArgument)),
        request.orderArguments());
    assertEquals(
        List.of(AttributeAssociation.of(this.mockFooAttribute, this.mockFilterArgument)),
        request.filterArguments());
    assertEquals(Optional.of(mockSpace), request.spaceId());
  }
}
