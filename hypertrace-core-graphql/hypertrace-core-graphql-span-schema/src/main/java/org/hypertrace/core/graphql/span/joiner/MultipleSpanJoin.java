package org.hypertrace.core.graphql.span.joiner;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import java.util.List;
import org.hypertrace.core.graphql.span.schema.Span;

public interface MultipleSpanJoin {
  String SPANS_KEY = "spans";

  @GraphQLField
  @GraphQLName(SPANS_KEY)
  List<Span> spans();
}
