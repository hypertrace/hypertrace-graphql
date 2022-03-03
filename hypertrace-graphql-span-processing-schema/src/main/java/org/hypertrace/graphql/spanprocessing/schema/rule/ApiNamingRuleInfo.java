package org.hypertrace.graphql.spanprocessing.schema.rule;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

@GraphQLName(ExcludeSpanRuleInfo.TYPE_NAME)
public interface ApiNamingRuleInfo {
  String TYPE_NAME = "ApiNamingRuleInfo";

  String NAME_KEY = "name";
  String SPAN_FILTER_KEY = "spanFilter";
  String DISABLED_KEY = "disabled";
  String REGEX_KEY = "regex";
  String VALUE_KEY = "value";

  @GraphQLField
  @GraphQLName(NAME_KEY)
  @GraphQLNonNull
  String name();

  @GraphQLField
  @GraphQLName(SPAN_FILTER_KEY)
  @GraphQLNonNull
  SpanProcessingRuleFilter spanFilter();

  @GraphQLField
  @GraphQLName(REGEX_KEY)
  @GraphQLNonNull
  String regex();

  @GraphQLField
  @GraphQLName(VALUE_KEY)
  @GraphQLNonNull
  String value();

  @GraphQLField
  @GraphQLName(DISABLED_KEY)
  @GraphQLNonNull
  boolean disabled();
}
