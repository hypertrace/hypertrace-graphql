package org.hypertrace.graphql.spanprocessing.schema.rule;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

@GraphQLName(IncludeSpanRuleInfo.TYPE_NAME)
public interface IncludeSpanRuleInfo {
  String TYPE_NAME = "IncludeSpanRuleInfo";

  String NAME_KEY = "name";
  String SPAN_FILTER_KEY = "spanFilter";
  String DISABLED_KEY = "disabled";

  @GraphQLField
  @GraphQLName(NAME_KEY)
  @GraphQLNonNull
  String name();

  @GraphQLField
  @GraphQLName(SPAN_FILTER_KEY)
  @GraphQLNonNull
  SpanProcessingRuleFilter spanFilter();

  @GraphQLField
  @GraphQLName(DISABLED_KEY)
  @GraphQLNonNull
  boolean disabled();
}
