package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(LeafCondition.TYPE_NAME)
public interface LeafCondition {
  String TYPE_NAME = "LeafCondition";

  String KEY_CONDITION_KEY = "KeyCondition";
  String VALUE_CONDITION_KEY = "ValueCondition";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(KEY_CONDITION_KEY)
  StringCondition keyCondition();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(VALUE_CONDITION_KEY)
  ValueCondition valueCondition();
}
