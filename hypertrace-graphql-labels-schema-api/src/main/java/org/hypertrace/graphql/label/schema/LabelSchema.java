package org.hypertrace.graphql.label.schema;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.label.fetcher.LabelApplicationRuleFetcher;
import org.hypertrace.graphql.label.fetcher.LabelFetcher;

public interface LabelSchema {
  String LABELS_QUERY_NAME = "labels";
  String LABEL_APPLICATION_RULE_QUERY_NAME = "labelApplicationRules";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(LABELS_QUERY_NAME)
  @GraphQLDataFetcher(LabelFetcher.class)
  LabelResultSet labels();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(LABEL_APPLICATION_RULE_QUERY_NAME)
  @GraphQLDataFetcher(LabelApplicationRuleFetcher.class)
  LabelApplicationRuleResultSet labelApplicationRules();
}
