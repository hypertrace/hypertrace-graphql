package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.ApiNamingCreateRuleMutator;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.ApiNamingDeleteRuleMutator;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.ApiNamingUpdateRuleMutator;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.ExcludeSpanCreateRuleMutator;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.ExcludeSpanDeleteRuleMutator;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.ExcludeSpanUpdateRuleMutator;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.IncludeSpanCreateRuleMutator;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.IncludeSpanDeleteRuleMutator;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.IncludeSpanUpdateRuleMutator;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.SamplingConfigCreateMutator;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.SamplingConfigDeleteMutator;
import org.hypertrace.graphql.spanprocessing.fetcher.mutation.SamplingConfigUpdateMutator;
import org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRule;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRule;
import org.hypertrace.graphql.spanprocessing.schema.rule.IncludeSpanRule;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.SamplingConfig;

public interface SpanProcessingMutationSchema {
  String CREATE_EXCLUDE_SPAN_RULE_MUTATION_NAME = "createExcludeSpanRule";
  String UPDATE_EXCLUDE_SPAN_RULE_MUTATION_NAME = "updateExcludeSpanRule";
  String DELETE_EXCLUDE_SPAN_RULE_MUTATION_NAME = "deleteExcludeSpanRule";
  String CREATE_INCLUDE_SPAN_RULE_MUTATION_NAME = "createIncludeSpanRule";
  String UPDATE_INCLUDE_SPAN_RULE_MUTATION_NAME = "updateIncludeSpanRule";
  String DELETE_INCLUDE_SPAN_RULE_MUTATION_NAME = "deleteIncludeSpanRule";
  String CREATE_API_NAMING_RULE_MUTATION_NAME = "createApiNamingRule";
  String UPDATE_API_NAMING_RULE_MUTATION_NAME = "updateApiNamingRule";
  String DELETE_API_NAMING_RULE_MUTATION_NAME = "deleteApiNamingRule";
  String CREATE_SAMPLING_CONFIG_MUTATION_NAME = "createSamplingConfig";
  String UPDATE_SAMPLING_CONFIG_MUTATION_NAME = "updateSamplingConfig";
  String DELETE_SAMPLING_CONFIG_MUTATION_NAME = "deleteSamplingConfig";

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

  @GraphQLField
  @GraphQLName(CREATE_INCLUDE_SPAN_RULE_MUTATION_NAME)
  @GraphQLNonNull
  @GraphQLDataFetcher(IncludeSpanCreateRuleMutator.class)
  IncludeSpanRule createIncludeSpanRule(
      @GraphQLName(IncludeSpanRuleCreate.ARGUMENT_NAME) @GraphQLNonNull
          IncludeSpanRuleCreate input);

  @GraphQLField
  @GraphQLName(UPDATE_INCLUDE_SPAN_RULE_MUTATION_NAME)
  @GraphQLNonNull
  @GraphQLDataFetcher(IncludeSpanUpdateRuleMutator.class)
  IncludeSpanRule updateIncludeSpanRule(
      @GraphQLName(IncludeSpanRuleUpdate.ARGUMENT_NAME) @GraphQLNonNull
          IncludeSpanRuleUpdate input);

  @GraphQLField
  @GraphQLName(DELETE_INCLUDE_SPAN_RULE_MUTATION_NAME)
  @GraphQLNonNull
  @GraphQLDataFetcher(IncludeSpanDeleteRuleMutator.class)
  DeleteSpanProcessingRuleResponse deleteIncludeSpanRule(
      @GraphQLName(IncludeSpanRuleDelete.ARGUMENT_NAME) @GraphQLNonNull
          IncludeSpanRuleDelete input);

  @GraphQLField
  @GraphQLName(CREATE_API_NAMING_RULE_MUTATION_NAME)
  @GraphQLNonNull
  @GraphQLDataFetcher(ApiNamingCreateRuleMutator.class)
  ApiNamingRule createApiNamingRule(
      @GraphQLName(ApiNamingRuleCreate.ARGUMENT_NAME) @GraphQLNonNull ApiNamingRuleCreate input);

  @GraphQLField
  @GraphQLName(UPDATE_API_NAMING_RULE_MUTATION_NAME)
  @GraphQLNonNull
  @GraphQLDataFetcher(ApiNamingUpdateRuleMutator.class)
  ApiNamingRule updateApiNamingRule(
      @GraphQLName(ApiNamingRuleUpdate.ARGUMENT_NAME) @GraphQLNonNull ApiNamingRuleUpdate input);

  @GraphQLField
  @GraphQLName(DELETE_API_NAMING_RULE_MUTATION_NAME)
  @GraphQLNonNull
  @GraphQLDataFetcher(ApiNamingDeleteRuleMutator.class)
  DeleteSpanProcessingRuleResponse deleteApiNamingRule(
      @GraphQLName(ApiNamingRuleDelete.ARGUMENT_NAME) @GraphQLNonNull ApiNamingRuleDelete input);

  @GraphQLField
  @GraphQLName(CREATE_SAMPLING_CONFIG_MUTATION_NAME)
  @GraphQLNonNull
  @GraphQLDataFetcher(SamplingConfigCreateMutator.class)
  SamplingConfig createSamplingConfig(
      @GraphQLName(SamplingConfigCreate.ARGUMENT_NAME) @GraphQLNonNull SamplingConfigCreate input);

  @GraphQLField
  @GraphQLName(UPDATE_SAMPLING_CONFIG_MUTATION_NAME)
  @GraphQLNonNull
  @GraphQLDataFetcher(SamplingConfigUpdateMutator.class)
  SamplingConfig updateSamplingConfig(
      @GraphQLName(SamplingConfigUpdate.ARGUMENT_NAME) @GraphQLNonNull SamplingConfigUpdate input);

  @GraphQLField
  @GraphQLName(DELETE_SAMPLING_CONFIG_MUTATION_NAME)
  @GraphQLNonNull
  @GraphQLDataFetcher(SamplingConfigDeleteMutator.class)
  DeleteSpanProcessingRuleResponse deleteSamplingConfig(
      @GraphQLName(SamplingConfigDelete.ARGUMENT_NAME) @GraphQLNonNull SamplingConfigDelete input);
}
