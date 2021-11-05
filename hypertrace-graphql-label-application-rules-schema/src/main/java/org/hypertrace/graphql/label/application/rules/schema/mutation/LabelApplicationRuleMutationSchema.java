package org.hypertrace.graphql.label.application.rules.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.label.application.rules.deserialization.LabelApplicationRuleIdArgument;
import org.hypertrace.graphql.label.application.rules.mutator.LabelApplicationRuleCreateMutator;
import org.hypertrace.graphql.label.application.rules.mutator.LabelApplicationRuleDeleteMutator;
import org.hypertrace.graphql.label.application.rules.mutator.LabelApplicationRuleUpdateMutator;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRule;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRuleData;

public interface LabelApplicationRuleMutationSchema {
  String CREATE_LABEL_APPLICATION_RULE = "createLabelApplicationRule";
  String UPDATE_LABEL_APPLICATION_RULE = "updateLabelApplicationRule";
  String DELETE_LABEL_APPLICATION_RULE = "deleteLabelApplicationRule";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(CREATE_LABEL_APPLICATION_RULE)
  @GraphQLDataFetcher(LabelApplicationRuleCreateMutator.class)
  LabelApplicationRule createLabelApplicationRule(
      @GraphQLNonNull @GraphQLName(LabelApplicationRuleData.ARGUMENT_NAME)
          LabelApplicationRuleData labelApplicationRuleData);

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(UPDATE_LABEL_APPLICATION_RULE)
  @GraphQLDataFetcher(LabelApplicationRuleUpdateMutator.class)
  LabelApplicationRule updateLabelApplicationRule(
      @GraphQLNonNull @GraphQLName(LabelApplicationRule.ARGUMENT_NAME)
          LabelApplicationRule labelApplicationRule);

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(DELETE_LABEL_APPLICATION_RULE)
  @GraphQLDataFetcher(LabelApplicationRuleDeleteMutator.class)
  Boolean deleteLabelApplicationRule(
      @GraphQLNonNull @GraphQLName(LabelApplicationRuleIdArgument.ARGUMENT_NAME) String id);
}
