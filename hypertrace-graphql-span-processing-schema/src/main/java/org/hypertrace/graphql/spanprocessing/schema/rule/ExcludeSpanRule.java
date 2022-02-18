package org.hypertrace.graphql.spanprocessing.schema.rule;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(ExcludeSpanRule.TYPE_NAME)
public interface ExcludeSpanRule extends Identifiable, ExcludeSpanRuleInfo {
  String TYPE_NAME = "ExcludeSpanRule";
}
