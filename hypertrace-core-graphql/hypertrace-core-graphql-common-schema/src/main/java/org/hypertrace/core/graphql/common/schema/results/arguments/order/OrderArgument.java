package org.hypertrace.core.graphql.common.schema.results.arguments.order;

import static java.util.Objects.requireNonNull;
import static org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression.forAttributeKey;

import graphql.annotations.annotationTypes.GraphQLDeprecate;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import java.util.Optional;
import javax.annotation.Nullable;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;

@GraphQLName(OrderArgument.TYPE_NAME)
public interface OrderArgument {
  String ARGUMENT_NAME = "orderBy"; // TODO rename to order
  String TYPE_NAME = "Order";
  String ORDER_KEY_NAME = "key";
  String ORDER_KEY_EXPRESSION_NAME = "keyExpression";
  String ORDER_DIRECTION_NAME = "direction";

  @GraphQLField
  @Nullable
  @GraphQLName(ORDER_KEY_NAME)
  @Deprecated
  @GraphQLDeprecate
  String key();

  @GraphQLField
  @GraphQLName(ORDER_KEY_EXPRESSION_NAME)
  @Nullable
  AttributeExpression keyExpression();

  @GraphQLField
  @GraphQLName(ORDER_DIRECTION_NAME)
  OrderDirection direction();

  // Temporary way of getting the expression from either keyExpression or key field
  default AttributeExpression resolvedKeyExpression() {
    return Optional.ofNullable(keyExpression())
        .orElseGet(() -> forAttributeKey(requireNonNull(key())));
  }
}
