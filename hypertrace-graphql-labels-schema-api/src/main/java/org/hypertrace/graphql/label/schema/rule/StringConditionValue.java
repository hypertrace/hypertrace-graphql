package org.hypertrace.graphql.label.schema.rule;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import javax.annotation.Nullable;

@GraphQLName(StringConditionValue.TYPE_NAME)
public interface StringConditionValue {
  String TYPE_NAME = "LabelApplicationStringConditionValue";
  String VALUE_KEY = "value";
  String VALUES_KEY = "values";
  String STRING_CONDITION_VALUE_TYPE_KEY = "stringConditionValueType";

  @GraphQLName(StringConditionValueType.TYPE_NAME)
  enum StringConditionValueType {
    VALUE,
    VALUES;
    private static final String TYPE_NAME = "StringConditionValueType";
  }

  @GraphQLField
  @Nullable
  @GraphQLName(VALUE_KEY)
  String value();

  @GraphQLField
  @Nullable
  @GraphQLName(VALUES_KEY)
  List<String> values();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(STRING_CONDITION_VALUE_TYPE_KEY)
  StringConditionValueType stringConditionValueType();
}
