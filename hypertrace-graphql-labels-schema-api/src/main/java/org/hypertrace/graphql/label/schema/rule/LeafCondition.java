package org.hypertrace.graphql.label.schema.rule;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(LeafCondition.TYPE_NAME)
public interface LeafCondition {
  String TYPE_NAME = "LabelApplicationLeafCondition";

  String KEY_CONDITION_KEY = "keyCondition";
  String VALUE_CONDITION_KEY = "valueCondition";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(KEY_CONDITION_KEY)
  StringCondition keyCondition();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(VALUE_CONDITION_KEY)
  ValueCondition valueCondition();
}
