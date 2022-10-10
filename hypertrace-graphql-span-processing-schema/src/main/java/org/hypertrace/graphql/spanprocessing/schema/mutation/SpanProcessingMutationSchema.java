package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.ExcludeSpanCreateRuleMutator;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.ExcludeSpanDeleteRuleMutator;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.ExcludeSpanUpdateRuleMutator;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRule;

public interface SpanProcessingMutationSchema {
  String CREATE_EXCLUDE_SPAN_RULE_MUTATION_NAME = "createExcludeSpanRule";
  String UPDATE_EXCLUDE_SPAN_RULE_MUTATION_NAME = "updateExcludeSpanRule";
  String DELETE_EXCLUDE_SPAN_RULE_MUTATION_NAME = "deleteExcludeSpanRule";

  @GraphQLField
  @GraphQLName(CREATE_EXCLUDE_SPAN_RULE_MUTATION_NAME)
  @GraphQLNonNull
  @GraphQLDataFetcher(ExcludeSpanCreateRuleMutator.class)
  ExcludeSpanRule createExcludeSpanRule(
      @GraphQLName(ExcludeSpanRuleCreate.ARGUMENT_NAME) @GraphQLNonNull
          ExcludeSpanRuleCreate input);

  @GraphQLField
  @GraphQLName(UPDATE_EXCLUDE_SPAN_RULE_MUTATION_NAME)
  @GraphQLNonNull
  @GraphQLDataFetcher(ExcludeSpanUpdateRuleMutator.class)
  ExcludeSpanRule updateExcludeSpanRule(
      @GraphQLName(ExcludeSpanRuleUpdate.ARGUMENT_NAME) @GraphQLNonNull
          ExcludeSpanRuleUpdate input);

  @GraphQLField
  @GraphQLName(DELETE_EXCLUDE_SPAN_RULE_MUTATION_NAME)
  @GraphQLNonNull
  @GraphQLDataFetcher(ExcludeSpanDeleteRuleMutator.class)
  DeleteSpanProcessingRuleResponse deleteExcludeSpanRule(
      @GraphQLName(ExcludeSpanRuleDelete.ARGUMENT_NAME) @GraphQLNonNull
          ExcludeSpanRuleDelete input);
}
