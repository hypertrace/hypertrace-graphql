package org.hypertrace.graphql.label.schema.query;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;

@GraphQLName(LabelResultSet.TYPE_NAME)
public interface LabelResultSet extends ResultSet<Label> {
  String TYPE_NAME = "LabelResultSet";

  @Override
  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(RESULT_SET_RESULTS_NAME)
  List<Label> results();
}
