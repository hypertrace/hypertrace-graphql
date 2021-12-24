package org.hypertrace.graphql.label.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.label.deserialization.LabelApplicationRuleIdArgument;
import org.hypertrace.graphql.label.mutator.LabelApplicationRuleCreateMutator;
import org.hypertrace.graphql.label.mutator.LabelApplicationRuleDeleteMutator;
import org.hypertrace.graphql.label.mutator.LabelApplicationRuleUpdateMutator;
import org.hypertrace.graphql.label.mutator.LabelCreateMutator;
import org.hypertrace.graphql.label.mutator.LabelUpdateMutator;
import org.hypertrace.graphql.label.schema.Label;
import org.hypertrace.graphql.label.schema.LabelApplicationRule;
import org.hypertrace.graphql.label.schema.LabelApplicationRuleData;

public interface LabelMutationSchema {
  String CREATE_LABEL = "createLabel";
  String UPDATE_LABEL = "updateLabel";
  String CREATE_LABEL_APPLICATION_RULE = "createLabelApplicationRule";
  String UPDATE_LABEL_APPLICATION_RULE = "updateLabelApplicationRule";
  String DELETE_LABEL_APPLICATION_RULE = "deleteLabelApplicationRule";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(CREATE_LABEL)
  @GraphQLDataFetcher(LabelCreateMutator.class)
  Label createLabel(@GraphQLNonNull @GraphQLName(CreateLabel.ARGUMENT_NAME) CreateLabel label);

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(UPDATE_LABEL)
  @GraphQLDataFetcher(LabelUpdateMutator.class)
  Label updateLabel(@GraphQLNonNull @GraphQLName(Label.ARGUMENT_NAME) UpdateLabel label);

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
