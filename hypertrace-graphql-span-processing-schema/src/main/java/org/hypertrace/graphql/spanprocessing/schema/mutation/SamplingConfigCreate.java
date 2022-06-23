package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.SamplingConfigInfo;

@GraphQLName(SamplingConfigCreate.TYPE_NAME)
public interface SamplingConfigCreate extends SamplingConfigInfo {
  String TYPE_NAME = "SamplingConfigCreate";
  String ARGUMENT_NAME = "input";
}
