package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRuleInfo;

@GraphQLName(ExcludeSpanRuleCreate.TYPE_NAME)
public interface ExcludeSpanRuleCreate extends ExcludeSpanRuleInfo {
  String TYPE_NAME = "ExcludeSpanRuleCreate";
  String ARGUMENT_NAME = "input";
}
