package org.hypertrace.graphql.spanprocessing.schema.rule;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

@GraphQLName(ExcludeSpanRuleInfo.TYPE_NAME)
public interface ExcludeSpanRuleInfo {
  String TYPE_NAME = "ExcludeSpanRuleInfo";

  String NAME_KEY = "name";
  String SPAN_FILTER_KEY = "spanFilter";
  String DISABLED_KEY = "disabled";
  String RULE_TYPE_KEY = "ruleType";

  @GraphQLField
  @GraphQLName(NAME_KEY)
  @GraphQLDescription("Exclude span rule name")
  @GraphQLNonNull
  String name();

  @GraphQLField
  @GraphQLName(SPAN_FILTER_KEY)
  @GraphQLDescription("Span processing rule filter contains filters based on span attributes")
  @GraphQLNonNull
  SpanProcessingRuleFilter spanFilter();

  @GraphQLField
  @GraphQLName(DISABLED_KEY)
  @GraphQLDescription(
      "Disabled field denotes whether the rule is disabled or not. By default it is enabled")
  @GraphQLNonNull
  boolean disabled();

  @GraphQLField
  @GraphQLName(RULE_TYPE_KEY)
  @GraphQLDescription(
      "Exclude span rule type tells us whether it is user configured rule or system rule")
  // TODO: make this field non-nullable
  ExcludeSpanRuleRuleType ruleType();
}
