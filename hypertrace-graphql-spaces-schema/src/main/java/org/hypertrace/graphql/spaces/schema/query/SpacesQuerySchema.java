package org.hypertrace.graphql.spaces.schema.query;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.spaces.fetcher.SpaceConfigRuleFetcher;

public interface SpacesQuerySchema {

  String SPACE_CONFIG_RULES_KEY = "spaceConfigRules";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(SPACE_CONFIG_RULES_KEY)
  @GraphQLDataFetcher(SpaceConfigRuleFetcher.class)
  SpaceConfigRuleResultSet spaceConfigRules();
}
