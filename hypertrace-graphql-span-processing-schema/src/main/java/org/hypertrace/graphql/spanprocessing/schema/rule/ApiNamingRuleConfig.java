package org.hypertrace.graphql.spanprocessing.schema.rule;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(ApiNamingRuleConfig.TYPE_NAME)
public interface ApiNamingRuleConfig {
  String TYPE_NAME = "ApiNamingRuleConfig";

  String API_NAMING_RULE_CONFIG_TYPE = "apiNamingRuleConfigType";
  String SEGMENT_MATCHING_BASED_RULE_CONFIG_TYPE = "segmentMatchingBasedRuleConfig";

  @GraphQLField
  @GraphQLName(API_NAMING_RULE_CONFIG_TYPE)
  @GraphQLNonNull
  ApiNamingRuleConfigType apiNamingRuleConfigType();

  @GraphQLField
  @GraphQLName(SEGMENT_MATCHING_BASED_RULE_CONFIG_TYPE)
  SegmentMatchingBasedRuleConfig segmentMatchingBasedRuleConfig();
}
