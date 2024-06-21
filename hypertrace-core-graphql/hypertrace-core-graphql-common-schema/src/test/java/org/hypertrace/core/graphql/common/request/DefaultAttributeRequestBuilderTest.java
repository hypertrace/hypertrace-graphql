package org.hypertrace.core.graphql.common.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeKeyArgument;
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
class DefaultAttributeRequestBuilderTest {
  @Mock AttributeStore mockAttributeStore;
  @Mock AttributeAssociator mockAttributeAssociator;
  @Mock ArgumentDeserializer mockArgumentDeserializer;
  @Mock GraphQlSelectionFinder mockSelectionFinder;
  @Mock GraphQlRequestContext mockContext;
  @Mock DataFetchingFieldSelectionSet mockSelectionSet;
  @Mock AttributeModel mockAttribute;
  @Mock SelectedField mockSelectedField;

  private DefaultAttributeRequestBuilder requestBuilder;

  @BeforeEach
  void beforeEach() {
    this.requestBuilder =
        new DefaultAttributeRequestBuilder(
            mockAttributeStore,
            mockAttributeAssociator,
            mockArgumentDeserializer,
            mockSelectionFinder);
  }

  @Test
  void canBuildRequestForSelectionSet() {
    AttributeAssociation<AttributeExpression> expectedResultExpression =
        AttributeAssociation.of(this.mockAttribute, AttributeExpression.forAttributeKey("fooKey"));
    when(this.mockSelectionFinder.findSelections(eq(this.mockSelectionSet), any()))
        .thenReturn(Stream.of(this.mockSelectedField));
    when(this.mockArgumentDeserializer.deserializePrimitive(any(), eq(AttributeKeyArgument.class)))
        .thenReturn(Optional.of("fooKey"));
    when(this.mockAttributeAssociator.associateAttribute(
            eq(this.mockContext),
            eq("SPAN"),
            eq(AttributeExpression.forAttributeKey("fooKey")),
            eq("fooKey")))
        .thenReturn(Single.just(expectedResultExpression));

    List<AttributeRequest> returned =
        this.requestBuilder
            .buildForAttributeQueryableSelectionSet(this.mockContext, "SPAN", this.mockSelectionSet)
            .toList()
            .blockingGet();

    assertEquals(1, returned.size());
    assertEquals(expectedResultExpression, returned.get(0).attributeExpressionAssociation());
  }
}
