package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(LabelApplicationRule.TYPE_NAME)
public interface LabelApplicationRule {
  String TYPE_NAME = "LabelApplicationRule";
  String ARGUMENT_NAME = "labelApplicationRule";

  String ID_KEY = "id";
  String LABEL_APPLICATION_RULE_DATA_KEY = "labelApplicationRuleData";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ID_KEY)
  String id();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(LABEL_APPLICATION_RULE_DATA_KEY)
  LabelApplicationRuleData labelApplicationRuleData();
}
