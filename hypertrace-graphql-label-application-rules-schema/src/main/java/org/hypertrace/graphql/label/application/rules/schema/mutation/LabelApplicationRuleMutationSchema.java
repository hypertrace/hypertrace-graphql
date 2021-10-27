package org.hypertrace.graphql.label.application.rules.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.label.application.rules.mutator.LabelApplicationRuleCreateMutator;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRule;

public interface LabelApplicationRuleMutationSchema {
  String CREATE_LABEL_APPLICATION_RULE = "createLabelApplicationRule";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(CREATE_LABEL_APPLICATION_RULE)
  @GraphQLDataFetcher(LabelApplicationRuleCreateMutator.class)
  LabelApplicationRule createLabelApplicationRule(
      @GraphQLNonNull @GraphQLName(CreateLabelApplicationRule.ARGUMENT_NAME)
          CreateLabelApplicationRule labelApplicationRule);
}
