package org.hypertrace.graphql.label.application.rules.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRuleData;

@GraphQLName(CreateLabelApplicationRule.TYPE_NAME)
public interface CreateLabelApplicationRule {
  String TYPE_NAME = "CreateLabelApplicationRule";
  String ARGUMENT_NAME = "LabelApplicationRuleCreateRequest";
  String LABEL_APPLICATION_RULE_DATA = "LabelApplicationRuleData";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(LABEL_APPLICATION_RULE_DATA)
  LabelApplicationRuleData labelApplicationRuleData();
}
