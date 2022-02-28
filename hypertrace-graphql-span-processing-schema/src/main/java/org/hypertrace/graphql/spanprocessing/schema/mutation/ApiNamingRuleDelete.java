package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(ApiNamingRuleDelete.TYPE_NAME)
public interface ApiNamingRuleDelete extends Identifiable {
  String TYPE_NAME = "ApiNamingRuleDelete";
  String ARGUMENT_NAME = "input";
}
