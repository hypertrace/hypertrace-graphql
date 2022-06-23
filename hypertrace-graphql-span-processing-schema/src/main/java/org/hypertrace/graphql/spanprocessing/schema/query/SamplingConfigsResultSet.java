package org.hypertrace.graphql.spanprocessing.schema.query;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.SamplingConfig;

@GraphQLName(SamplingConfigsResultSet.TYPE_NAME)
public interface SamplingConfigsResultSet extends ResultSet<SamplingConfig> {
  String TYPE_NAME = "SamplingConfigsResultSet";

  @Override
  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(RESULT_SET_RESULTS_NAME)
  List<SamplingConfig> results();
}
