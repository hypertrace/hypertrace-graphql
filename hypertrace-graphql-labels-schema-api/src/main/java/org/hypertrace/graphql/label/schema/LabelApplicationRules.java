package org.hypertrace.graphql.label.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.LimitArgument;
import org.hypertrace.graphql.label.schema.rule.LabelApplicationRuleResultSet;

@GraphQLName(LabelApplicationRules.TYPE_NAME)
public interface LabelApplicationRules {
  String TYPE_NAME = "LabelApplicationRules";
  String LABEL_APPLICATION_RULES_QUERY_NAME = "labelApplicationRules";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(LABEL_APPLICATION_RULES_QUERY_NAME)
  LabelApplicationRuleResultSet labelApplicationRules(
      @GraphQLName(LimitArgument.ARGUMENT_NAME) int limit);
}
