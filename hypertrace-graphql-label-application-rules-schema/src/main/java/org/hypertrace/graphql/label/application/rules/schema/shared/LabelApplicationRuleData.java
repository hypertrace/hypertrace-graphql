package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(LabelApplicationRuleData.TYPE_NAME)
public interface LabelApplicationRuleData {
  String TYPE_NAME = "LabelApplicationRuleData";

  String NAME_KEY = "name";
  String CONDITION_KEY = "condition";
  String ACTION_KEY = "action";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(NAME_KEY)
  String name();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(CONDITION_KEY)
  Condition condition();
}
