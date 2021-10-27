package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import javax.annotation.Nullable;

@GraphQLName(ValueCondition.TYPE_NAME)
public interface ValueCondition {
  String TYPE_NAME = "ValueCondition";

  String STRING_CONDITION_KEY = "stringCondition";
  String UNARY_CONDITION_KEY = "unaryCondition";

  enum ValueConditionType {
    STRING_CONDITION,
    UNARY_CONDITION;

    public static final String TYPE_NAME = "ValueConditionType";
  }

  @GraphQLField
  @GraphQLName(STRING_CONDITION_KEY)
  @Nullable
  StringCondition stringCondition();

  @GraphQLField
  @GraphQLName(UNARY_CONDITION_KEY)
  @Nullable
  UnaryCondition unaryCondition();

  @GraphQLField
  @GraphQLName(ValueConditionType.TYPE_NAME)
  @GraphQLNonNull
  ValueConditionType valueConditionType();
}
