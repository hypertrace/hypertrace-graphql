package org.hypertrace.core.graphql.common.schema.results.arguments.order;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(OrderArgument.TYPE_NAME)
public interface OrderArgument {
  String ARGUMENT_NAME = "orderBy"; // TODO rename to order
  String TYPE_NAME = "Order";
  String ORDER_KEY_NAME = "key";
  String ORDER_DIRECTION_NAME = "direction";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ORDER_KEY_NAME)
  String key();

  @GraphQLField
  @GraphQLName(ORDER_DIRECTION_NAME)
  OrderDirection direction();
}
