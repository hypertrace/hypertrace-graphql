package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

@GraphQLName(ExcludeSpanRuleUpdate.TYPE_NAME)
public interface ExcludeSpanRuleUpdate extends Identifiable {
  String TYPE_NAME = "ExcludeSpanRuleUpdate";
  String ARGUMENT_NAME = "input";

  String NAME_KEY = "name";
  String SPAN_PROCESSING_FILTER_KEY = "spanFilter";
  String DISABLED_KEY = "disabled";

  @GraphQLField
  @GraphQLName(NAME_KEY)
  @GraphQLDescription("Update the rule name for the provided ruleId")
  @GraphQLNonNull
  String name();

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_FILTER_KEY)
  @GraphQLDescription("Update the existing spanFilters for the input ruleId")
  @GraphQLNonNull
  SpanProcessingRuleFilter spanFilter();

  @GraphQLField
  @GraphQLName(DISABLED_KEY)
  @GraphQLDescription("Enable or disable the rule")
  @GraphQLNonNull
  boolean disabled();
}
