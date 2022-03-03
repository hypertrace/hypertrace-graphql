package org.hypertrace.graphql.spanprocessing.schema.query;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;
import org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRule;

@GraphQLName(ApiNamingRuleResultSet.TYPE_NAME)
public interface ApiNamingRuleResultSet extends ResultSet<ApiNamingRule> {
  String TYPE_NAME = "ApiNamingRuleResultSet";

  @Override
  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(RESULT_SET_RESULTS_NAME)
  List<ApiNamingRule> results();
}
