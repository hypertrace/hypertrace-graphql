package org.hypertrace.graphql.label.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(Condition.TYPE_NAME)
public interface Condition {
  String TYPE_NAME = "LabelApplicationCondition";

  String LEAF_CONDITION_KEY = "leafCondition";

  @GraphQLField
  @GraphQLName(LEAF_CONDITION_KEY)
  @GraphQLNonNull
  LeafCondition leafCondition();
}
