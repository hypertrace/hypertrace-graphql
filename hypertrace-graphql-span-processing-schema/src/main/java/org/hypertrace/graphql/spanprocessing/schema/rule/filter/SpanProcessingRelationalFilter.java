package org.hypertrace.graphql.spanprocessing.schema.rule.filter;

import static org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument.FILTER_ARGUMENT_VALUE;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(SpanProcessingRelationalFilter.TYPE_NAME)
public interface SpanProcessingRelationalFilter {
  String TYPE_NAME = "SpanProcessingRelationalFilter";

  String RELATION_OPERATOR_KEY = "relationalOperator";
  String SPAN_PROCESSING_FILTER_FIELD_KEY = "field";
  String SPAN_PROCESSING_FILTER_KEY_KEY = "key";

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_FILTER_KEY_KEY)
  String key();

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_FILTER_FIELD_KEY)
  SpanProcessingFilterField field();

  @GraphQLField
  @GraphQLName(RELATION_OPERATOR_KEY)
  @GraphQLNonNull
  SpanProcessingRelationalOperator relationalOperator();

  @GraphQLField
  @GraphQLName(FILTER_ARGUMENT_VALUE)
  @GraphQLNonNull
  Object value();
}
