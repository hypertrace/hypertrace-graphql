package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;

@GraphQLName(LabelApplicationRuleData.TYPE_NAME)
public interface LabelApplicationRuleData {
  String TYPE_NAME = "LabelApplicationRuleData";
  String ARGUMENT_NAME = "labelApplicationRuleData";

  String NAME_KEY = "name";
  String CONDITION_LIST_KEY = "conditionList";
  String ACTION_KEY = "action";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(NAME_KEY)
  String name();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(CONDITION_LIST_KEY)
  List<Condition> conditionList();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ACTION_KEY)
  Action action();
}
