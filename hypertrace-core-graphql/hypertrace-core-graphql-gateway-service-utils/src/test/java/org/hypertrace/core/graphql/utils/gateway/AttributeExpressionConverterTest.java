package org.hypertrace.core.graphql.utils.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AttributeExpressionConverterTest {
  @Mock AttributeModel mockAttribute;

  @Test
  void convertsAttributeExpressionWithSubpath() {
    org.hypertrace.gateway.service.v1.common.AttributeExpression expectedAttributeExpression =
        org.hypertrace.gateway.service.v1.common.AttributeExpression.newBuilder()
            .setAttributeId("EVENT.tags")
            .setAlias("tags.spantag")
            .setSubpath("spantag")
            .build();

    when(mockAttribute.id()).thenReturn("EVENT.tags");
    AttributeExpressionConverter attributeExpressionConverter = new AttributeExpressionConverter();

    assertEquals(
        Expression.newBuilder().setAttributeExpression(expectedAttributeExpression).build(),
        attributeExpressionConverter
            .convert(
                AttributeAssociation.of(
                    this.mockAttribute, new AttributeExpression("tags", "spantag")))
            .blockingGet());
  }

  @Test
  void convertsAttributeExpressionWithoutSubpath() {
    org.hypertrace.gateway.service.v1.common.AttributeExpression expectedAttributeExpression =
        org.hypertrace.gateway.service.v1.common.AttributeExpression.newBuilder()
            .setAttributeId("EVENT.tags")
            .setAlias("tags")
            .build();

    when(mockAttribute.id()).thenReturn("EVENT.tags");
    AttributeExpressionConverter attributeExpressionConverter = new AttributeExpressionConverter();

    assertEquals(
        Expression.newBuilder().setAttributeExpression(expectedAttributeExpression).build(),
        attributeExpressionConverter
            .convert(
                AttributeAssociation.of(
                    this.mockAttribute, AttributeExpression.forAttributeKey("tags")))
            .blockingGet());
  }
}
