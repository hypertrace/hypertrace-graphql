package org.hypertrace.core.graphql.span.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;

@GraphQLName(SpanResultSet.TYPE_NAME)
public interface SpanResultSet extends ResultSet<Span> {
  String TYPE_NAME = "SpanResultSet";

  @Override
  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(RESULT_SET_RESULTS_NAME)
  List<Span> results();
}
