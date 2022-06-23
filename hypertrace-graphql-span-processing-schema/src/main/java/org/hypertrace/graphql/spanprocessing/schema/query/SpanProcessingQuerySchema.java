package org.hypertrace.graphql.spanprocessing.schema.query;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.spanprocessing.fetcher.query.ApiNamingRulesFetcher;
import org.hypertrace.graphql.spanprocessing.fetcher.query.ExcludeSpanRulesFetcher;
import org.hypertrace.graphql.spanprocessing.fetcher.query.IncludeSpanRulesFetcher;
import org.hypertrace.graphql.spanprocessing.fetcher.query.SamplingConfigsFetcher;

public interface SpanProcessingQuerySchema {
  String EXCLUDE_SPAN_RULES_QUERY_NAME = "excludeSpanRules";
  String INCLUDE_SPAN_RULES_QUERY_NAME = "includeSpanRules";
  String API_NAMING_RULES_QUERY_NAME = "apiNamingRules";
  String SAMPLING_CONFIGS_QUERY_NAME = "samplingConfigs";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(EXCLUDE_SPAN_RULES_QUERY_NAME)
  @GraphQLDataFetcher(ExcludeSpanRulesFetcher.class)
  ExcludeSpanRuleResultSet excludeSpanRules();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(INCLUDE_SPAN_RULES_QUERY_NAME)
  @GraphQLDataFetcher(IncludeSpanRulesFetcher.class)
  IncludeSpanRuleResultSet includeSpanRules();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(API_NAMING_RULES_QUERY_NAME)
  @GraphQLDataFetcher(ApiNamingRulesFetcher.class)
  ApiNamingRuleResultSet apiNamingRules();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(SAMPLING_CONFIGS_QUERY_NAME)
  @GraphQLDataFetcher(SamplingConfigsFetcher.class)
  SamplingConfigsResultSet samplingConfigs();
}
