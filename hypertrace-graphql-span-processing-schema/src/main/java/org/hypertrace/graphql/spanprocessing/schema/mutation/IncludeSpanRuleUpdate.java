package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

@GraphQLName(IncludeSpanRuleUpdate.TYPE_NAME)
public interface IncludeSpanRuleUpdate extends Identifiable {
  String TYPE_NAME = "IncludeSpanRuleUpdate";
  String ARGUMENT_NAME = "input";

  String NAME_KEY = "name";
  String SPAN_PROCESSING_FILTER_KEY = "spanFilter";
  String DISABLED_KEY = "disabled";

  @GraphQLField
  @GraphQLName(NAME_KEY)
  @GraphQLNonNull
  String name();

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_FILTER_KEY)
  @GraphQLNonNull
  SpanProcessingRuleFilter spanFilter();

  @GraphQLField
  @GraphQLName(DISABLED_KEY)
  @GraphQLNonNull
  boolean disabled();
}
