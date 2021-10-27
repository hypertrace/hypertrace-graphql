package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import javax.annotation.Nullable;

@GraphQLName(Condition.TYPE_NAME)
public interface Condition {
  String TYPE_NAME = "Condition";

  String LEAF_CONDITION_KEY = "leafCondition";
  String COMPOSITE_CONDITION_KEY = "compositeCondition";

  enum ConditionType {
    LEAF_CONDITION,
    COMPOSITE_CONDITION;

    public static final String TYPE_NAME = "ConditionType";
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
  @GraphQLName(ConditionType.TYPE_NAME)
  @GraphQLNonNull
  ConditionType conditionType();
}
