package org.hypertrace.graphql.label.schema.rule;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(StringCondition.TYPE_NAME)
public interface StringCondition {
  String TYPE_NAME = "LabelApplicationStringCondition";

  String OPERATOR_KEY = "operator";
  String STRING_CONDITION_VALUE_KEY = "stringConditionValue";

  @GraphQLName(Operator.TYPE_NAME)
  enum Operator {
    OPERATOR_EQUALS,
    OPERATOR_MATCHES_REGEX,
    OPERATOR_MATCHES_IPS,
    OPERATOR_NOT_MATCHES_IPS;
    private static final String TYPE_NAME = "StringConditionOperator";
  }

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(OPERATOR_KEY)
  Operator operator();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(STRING_CONDITION_VALUE_KEY)
  StringConditionValue stringConditionValue();
}
