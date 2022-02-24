package org.hypertrace.graphql.spanprocessing.schema.rule;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(ExcludeSpanRuleDetails.TYPE_NAME)
public interface ExcludeSpanRuleDetails extends ExcludeSpanRule, ExcludeSpanRuleMetadata {
  String TYPE_NAME = "ExcludeSpanRuleDetails";
}
