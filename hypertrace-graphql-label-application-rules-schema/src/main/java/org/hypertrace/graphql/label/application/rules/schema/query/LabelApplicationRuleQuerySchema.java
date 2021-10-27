package org.hypertrace.graphql.label.application.rules.schema.query;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.label.application.rules.fetcher.LabelApplicationRuleFetcher;

public interface LabelApplicationRuleQuerySchema {
  String LABEL_APPLICATION_RULE_QUERY_NAME = "labelApplicationRules";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(LABEL_APPLICATION_RULE_QUERY_NAME)
  @GraphQLDataFetcher(LabelApplicationRuleFetcher.class)
  LabelApplicationRuleResultSet labelApplicationRules();
}
