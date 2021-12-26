package org.hypertrace.graphql.label.schema.rule;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;

@GraphQLName(LabelApplicationRuleResultSet.TYPE_NAME)
public interface LabelApplicationRuleResultSet extends ResultSet<LabelApplicationRule> {
  String TYPE_NAME = "LabelApplicationRuleResultSet";

  @Override
  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(RESULT_SET_RESULTS_NAME)
  List<LabelApplicationRule> results();
}
