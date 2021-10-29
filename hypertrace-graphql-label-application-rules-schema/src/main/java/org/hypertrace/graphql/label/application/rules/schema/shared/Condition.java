package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import javax.annotation.Nullable;

@GraphQLName(Condition.TYPE_NAME)
public interface Condition {
  String TYPE_NAME = "LabelApplicationCondition";

  String LEAF_CONDITION_KEY = "leafCondition";
  String COMPOSITE_CONDITION_KEY = "compositeCondition";
  String CONDITION_TYPE_KEY = "conditionType";

  @GraphQLName(ConditionType.TYPE_NAME)
  enum ConditionType {
    LEAF_CONDITION,
    COMPOSITE_CONDITION;
    private static final String TYPE_NAME = "ConditionType";
  }

  @GraphQLField
  @GraphQLName(LEAF_CONDITION_KEY)
  @Nullable
  LeafCondition leafCondition();

  @GraphQLField
  @GraphQLName(COMPOSITE_CONDITION_KEY)
  @Nullable
  CompositeCondition compositeCondition();

  @GraphQLField
  @GraphQLName(CONDITION_TYPE_KEY)
  @GraphQLNonNull
  ConditionType conditionType();
}
