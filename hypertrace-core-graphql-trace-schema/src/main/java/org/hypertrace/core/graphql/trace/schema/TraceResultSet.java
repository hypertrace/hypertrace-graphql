package org.hypertrace.core.graphql.trace.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;

@GraphQLName(TraceResultSet.TYPE_NAME)
public interface TraceResultSet extends ResultSet<Trace> {
  String TYPE_NAME = "TraceResultSet";

  @Override
  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(RESULT_SET_RESULTS_NAME)
  List<Trace> results();
}
