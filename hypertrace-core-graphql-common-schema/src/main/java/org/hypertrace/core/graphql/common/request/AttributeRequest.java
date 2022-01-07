package org.hypertrace.core.graphql.common.request;

import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;

public interface AttributeRequest {
  AttributeAssociation<AttributeExpression> attributeExpression();

  default String asMapKey() {
    return attributeExpression().value().asAlias();
  }
}
