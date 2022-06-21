package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.graphql.spanprocessing.schema.rule.IncludeSpanRuleInfo;

@GraphQLName(IncludeSpanRuleCreate.TYPE_NAME)
public interface IncludeSpanRuleCreate extends IncludeSpanRuleInfo {
  String TYPE_NAME = "IncludeSpanRuleCreate";
  String ARGUMENT_NAME = "input";
}
