package org.hypertrace.graphql.label.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(UnaryCondition.TYPE_NAME)
public interface UnaryCondition {
  String TYPE_NAME = "LabelApplicationUnaryCondition";

  String OPERATOR_KEY = "operator";

  @GraphQLName(Operator.TYPE_NAME)
  enum Operator {
    OPERATOR_EXISTS;
    private static final String TYPE_NAME = "UnaryConditionOperator";
  }

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(OPERATOR_KEY)
  Operator operator();
}
