package org.hypertrace.core.graphql.common.request;

import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;

public interface AttributeRequest {
  AttributeAssociation<AttributeExpression> attributeExpressionAssociation();

  default String asMapKey() {
    return attributeExpressionAssociation().value().asAlias();
  }
}
