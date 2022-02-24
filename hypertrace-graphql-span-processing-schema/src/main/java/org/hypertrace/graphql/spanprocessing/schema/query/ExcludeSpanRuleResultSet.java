package org.hypertrace.graphql.spanprocessing.schema.query;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRuleDetails;

@GraphQLName(ExcludeSpanRuleResultSet.TYPE_NAME)
public interface ExcludeSpanRuleResultSet extends ResultSet<ExcludeSpanRuleDetails> {
  String TYPE_NAME = "ExcludeSpanRuleResultSet";

  @Override
  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(RESULT_SET_RESULTS_NAME)
  List<ExcludeSpanRuleDetails> results();
}
