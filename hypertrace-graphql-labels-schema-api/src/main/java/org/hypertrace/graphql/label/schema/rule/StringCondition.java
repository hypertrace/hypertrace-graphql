package org.hypertrace.graphql.label.schema.rule;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import javax.annotation.Nullable;

@GraphQLName(StringCondition.TYPE_NAME)
public interface StringCondition {
  String TYPE_NAME = "LabelApplicationStringCondition";

  String OPERATOR_KEY = "operator";
  String VALUE_KEY = "value";
  String VALUES_KEY = "values";
  String STRING_CONDITION_VALUE_TYPE_KEY = "stringConditionValueType";

  @GraphQLName(Operator.TYPE_NAME)
  enum Operator {
    OPERATOR_EQUALS,
    OPERATOR_MATCHES_REGEX,
    OPERATOR_MATCHES_IPS,
    OPERATOR_NOT_MATCHES_IPS;
    private static final String TYPE_NAME = "StringConditionOperator";
  }

  @GraphQLName(StringConditionValueType.TYPE_NAME)
  enum StringConditionValueType {
    VALUE,
    VALUES;
    private static final String TYPE_NAME = "StringConditionValueType";
  }

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(OPERATOR_KEY)
  Operator operator();

  @GraphQLField
  @Nullable
  @GraphQLName(VALUE_KEY)
  String value();

  @GraphQLField
  @Nullable
  @GraphQLName(VALUES_KEY)
  List<String> values();

  @GraphQLField
  @Nullable
  @GraphQLName(STRING_CONDITION_VALUE_TYPE_KEY)
  StringConditionValueType stringConditionValueType();
}
