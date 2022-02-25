package org.hypertrace.graphql.spanprocessing.schema.mutation;

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
  String name();

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_FILTER_KEY)
  SpanProcessingRuleFilter spanFilter();

  @GraphQLField
  @GraphQLName(DISABLED_KEY)
  @GraphQLNonNull
  boolean disabled();
}
