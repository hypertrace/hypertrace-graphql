package org.hypertrace.graphql.spanprocessing.schema.query;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.spanprocessing.fetcher.query.ExcludeSpanRulesFetcher;

public interface SpanProcessingQuerySchema {
  String EXCLUDE_SPAN_RULES_QUERY_NAME = "excludeSpanRules";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(EXCLUDE_SPAN_RULES_QUERY_NAME)
  @GraphQLDescription("Get all exclude span rules")
  @GraphQLDataFetcher(ExcludeSpanRulesFetcher.class)
  ExcludeSpanRuleResultSet excludeSpanRules();
}
