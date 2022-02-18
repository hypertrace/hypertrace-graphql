package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(ExcludeSpanRuleDelete.TYPE_NAME)
public interface ExcludeSpanRuleDelete extends Identifiable {
  String TYPE_NAME = "ExcludeSpanRuleDelete";
  String ARGUMENT_NAME = "input";
}
