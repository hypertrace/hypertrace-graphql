package org.hypertrace.core.graphql.span.joiner;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.core.graphql.span.schema.Span;

public interface SpanJoin {
  String SPAN_KEY = "span";

  @GraphQLField
  @GraphQLName(SPAN_KEY)
  Span span();
}
