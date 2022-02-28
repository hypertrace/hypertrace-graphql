package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRuleInfo;

@GraphQLName(ApiNamingRuleCreate.TYPE_NAME)
public interface ApiNamingRuleCreate extends ApiNamingRuleInfo {
  String TYPE_NAME = "ApiNamingRuleCreate";
  String ARGUMENT_NAME = "input";
}
