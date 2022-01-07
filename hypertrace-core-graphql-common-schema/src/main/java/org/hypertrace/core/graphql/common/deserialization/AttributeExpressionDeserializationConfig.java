package org.hypertrace.core.graphql.common.deserialization;

import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;

class AttributeExpressionDeserializationConfig implements ArgumentDeserializationConfig {

  @Override
  public String getArgumentKey() {
    return AttributeExpression.ARGUMENT_NAME;
  }

  @Override
  public Class<AttributeExpression> getArgumentSchema() {
    return AttributeExpression.class;
  }
}
