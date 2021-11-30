package org.hypertrace.graphql.entity.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultEntityLabelRequestBuilderTest {

  private static final String SCOPE = "scope";

  @Mock GraphQlSelectionFinder mockSelectionFinder;
  @Mock AttributeRequestBuilder attributeRequestBuilder;
  @Mock GraphQlRequestContext mockRequestContext;

  EntityLabelRequestBuilder entityLabelRequestBuilder;

  @BeforeEach
  void beforeEach() {
    this.entityLabelRequestBuilder =
        spy(new DefaultEntityLabelRequestBuilder(attributeRequestBuilder, mockSelectionFinder));
  }

  @Test
  void test_buildLabelRequestIfPresentInResultSet_returnsEmpty() {
    DataFetchingFieldSelectionSet mockSelectionSet = mock(DataFetchingFieldSelectionSet.class);
    when(mockSelectionFinder.findSelections(eq(mockSelectionSet), any()))
        .thenReturn(Stream.empty());
    Optional<EntityLabelRequest> entityLabelRequestOptional =
        this.entityLabelRequestBuilder
            .buildLabelRequestIfPresentInResultSet(mockRequestContext, SCOPE, mockSelectionSet)
            .blockingGet();
    assertTrue(entityLabelRequestOptional.isEmpty());
  }

  @Test
  void test_buildLabelRequestIfPresentInResultSet_returnsLabelRequest() {
    DataFetchingFieldSelectionSet mockSelectionSet = mock(DataFetchingFieldSelectionSet.class);
    when(mockSelectionFinder.findSelections(eq(mockSelectionSet), any()))
        .thenReturn(Stream.of(mock(SelectedField.class)));
    AttributeRequest mockAttributeRequest = mock(AttributeRequest.class);
    when(attributeRequestBuilder.buildForAttributeExpression(
            eq(mockRequestContext), any(), eq(AttributeExpression.forAttributeKey("labels"))))
        .thenReturn(Single.just(mockAttributeRequest));
    Optional<EntityLabelRequest> entityLabelRequestOptional =
        this.entityLabelRequestBuilder
            .buildLabelRequestIfPresentInResultSet(mockRequestContext, SCOPE, mockSelectionSet)
            .blockingGet();
    assertEquals(
        mockAttributeRequest, entityLabelRequestOptional.get().labelIdArrayAttributeRequest());
  }

  @Test
  void test_buildLabelRequestIfPresentInAnyEntity_returnsLabelRequest() {
    SelectedField mockSelectionField = mock(SelectedField.class);
    DataFetchingFieldSelectionSet mockSelectionSet = mock(DataFetchingFieldSelectionSet.class);
    when(mockSelectionField.getSelectionSet()).thenReturn(mockSelectionSet);
    when(mockSelectionFinder.findSelections(eq(mockSelectionSet), any()))
        .thenReturn(Stream.of(mock(SelectedField.class)));
    AttributeRequest mockAttributeRequest = mock(AttributeRequest.class);
    when(attributeRequestBuilder.buildForAttributeExpression(
            eq(mockRequestContext), any(), eq(AttributeExpression.forAttributeKey("labels"))))
        .thenReturn(Single.just(mockAttributeRequest));
    Optional<EntityLabelRequest> entityLabelRequestOptional =
        this.entityLabelRequestBuilder
            .buildLabelRequestIfPresentInAnyEntity(
                mockRequestContext, SCOPE, List.of(mockSelectionField))
            .blockingGet();
    assertEquals(
        mockAttributeRequest, entityLabelRequestOptional.get().labelIdArrayAttributeRequest());
  }
}
