package org.hypertrace.graphql.spaces.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.spaces.mutator.SpaceConfigRuleCreationMutator;
import org.hypertrace.graphql.spaces.schema.shared.SpaceConfigRule;
import org.hypertrace.graphql.spaces.schema.shared.SpaceConfigRuleDefinition;

public interface SpacesMutationSchema {

  String CREATE_SPACE_CONFIG_RULE_KEY = "createSpaceConfigRule";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(CREATE_SPACE_CONFIG_RULE_KEY)
  @GraphQLDataFetcher(SpaceConfigRuleCreationMutator.class)
  SpaceConfigRule createSpaceConfigRule(
      @GraphQLNonNull @GraphQLName(SpaceConfigRuleDefinition.ARGUMENT_NAME)
          SpaceConfigRuleDefinition definition);
}
