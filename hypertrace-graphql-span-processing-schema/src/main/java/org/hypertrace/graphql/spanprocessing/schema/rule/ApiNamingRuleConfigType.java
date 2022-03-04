package org.hypertrace.graphql.spanprocessing.schema.rule;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(ApiNamingRuleConfigType.TYPE_NAME)
public enum ApiNamingRuleConfigType {
  SEGMENT_MATCHING;
  static final String TYPE_NAME = "ApiNamingRuleConfigType";
}
