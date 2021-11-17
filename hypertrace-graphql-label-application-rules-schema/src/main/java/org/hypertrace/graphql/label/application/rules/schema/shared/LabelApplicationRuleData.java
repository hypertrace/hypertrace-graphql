package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import javax.annotation.Nullable;

@GraphQLName(LabelApplicationRuleData.TYPE_NAME)
public interface LabelApplicationRuleData {
  String TYPE_NAME = "LabelApplicationRuleData";
  String ARGUMENT_NAME = "labelApplicationRuleData";

  String NAME_KEY = "name";
  String CONDITION_LIST_KEY = "conditionList";
  String ACTION_KEY = "action";
  String ENABLED_KEY = "enabled";
  String DESCRIPTION_KEY = "description";

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

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ENABLED_KEY)
  boolean enabled();

  @GraphQLField
  @Nullable
  @GraphQLName(DESCRIPTION_KEY)
  String description();
}
