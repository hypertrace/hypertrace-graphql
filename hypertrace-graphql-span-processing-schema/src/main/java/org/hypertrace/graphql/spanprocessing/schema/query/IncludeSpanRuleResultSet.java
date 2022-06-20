package org.hypertrace.graphql.spanprocessing.schema.query;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;
import org.hypertrace.graphql.spanprocessing.schema.rule.IncludeSpanRule;

@GraphQLName(IncludeSpanRuleResultSet.TYPE_NAME)
public interface IncludeSpanRuleResultSet extends ResultSet<IncludeSpanRule> {
  String TYPE_NAME = "IncludeSpanRuleResultSet";

  @Override
  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(RESULT_SET_RESULTS_NAME)
  List<IncludeSpanRule> results();
}
