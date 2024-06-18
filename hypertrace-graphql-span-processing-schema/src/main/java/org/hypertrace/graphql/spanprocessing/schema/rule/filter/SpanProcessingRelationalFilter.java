package org.hypertrace.graphql.spanprocessing.schema.rule.filter;

import static org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument.FILTER_ARGUMENT_VALUE;

import graphql.annotations.annotationTypes.GraphQLDescription;
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
  @GraphQLDescription(
      "Span processing filter key is the span attribute provided as string like http.request.body.email")
  String key();

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_FILTER_FIELD_KEY)
  @GraphQLDescription(
      "Span processing filter field supports a list of pre-defined fields like environment name, service name, url and url path (url without query params)")
  SpanProcessingFilterField field();

  @GraphQLField
  @GraphQLName(RELATION_OPERATOR_KEY)
  @GraphQLDescription(
      "Span processing relational operator which combines the lhs (key or field) and rhs (value)")
  @GraphQLNonNull
  SpanProcessingRelationalOperator relationalOperator();

  @GraphQLField
  @GraphQLName(FILTER_ARGUMENT_VALUE)
  @GraphQLDescription(
      "Span processing filter value to be evaluated using the relational operator for the provided lhs (key or field)")
  @GraphQLNonNull
  Object value();
}
