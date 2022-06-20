package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(IncludeSpanRuleDelete.TYPE_NAME)
public interface IncludeSpanRuleDelete extends Identifiable {
  String TYPE_NAME = "IncludeSpanRuleDelete";
  String ARGUMENT_NAME = "input";
}
