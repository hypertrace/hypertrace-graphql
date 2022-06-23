package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(SamplingConfigDelete.TYPE_NAME)
public interface SamplingConfigDelete extends Identifiable {
  String TYPE_NAME = "SamplingConfigDelete";
  String ARGUMENT_NAME = "input";
}
