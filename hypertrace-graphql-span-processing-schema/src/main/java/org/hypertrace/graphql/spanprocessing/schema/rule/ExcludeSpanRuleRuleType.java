package org.hypertrace.graphql.spanprocessing.schema.rule;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(ExcludeSpanRuleRuleType.TYPE_NAME)
public enum ExcludeSpanRuleRuleType {
  SYSTEM,
  USER,
  ;
  static final String TYPE_NAME = "ExcludeSpanRuleRuleType";
}
