package org.hypertrace.graphql.spanprocessing.schema.query;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.spanprocessing.fetcher.query.ApiNamingRulesFetcher;
import org.hypertrace.graphql.spanprocessing.fetcher.query.ExcludeSpanRulesFetcher;

public interface SpanProcessingQuerySchema {
  String EXCLUDE_SPAN_RULES_QUERY_NAME = "excludeSpanRules";
  String API_NAMING_RULES_QUERY_NAME = "apiNamingRules";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(EXCLUDE_SPAN_RULES_QUERY_NAME)
  @GraphQLDataFetcher(ExcludeSpanRulesFetcher.class)
  ExcludeSpanRuleResultSet excludeSpanRules();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(API_NAMING_RULES_QUERY_NAME)
  @GraphQLDataFetcher(ApiNamingRulesFetcher.class)
  ApiNamingRuleResultSet apiNamingRules();
}
