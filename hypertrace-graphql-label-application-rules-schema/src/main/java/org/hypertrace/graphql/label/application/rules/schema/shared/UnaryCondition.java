package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(UnaryCondition.TYPE_NAME)
public interface UnaryCondition {
  String TYPE_NAME = "UnaryCondition";

  String OPERATOR_KEY = "Operator";

  @GraphQLName(Operator.TYPE_NAME)
  enum Operator {
    OPERATOR_EXISTS;
    static final String TYPE_NAME = "Operator";
  }

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(OPERATOR_KEY)
  Operator operator();
}
