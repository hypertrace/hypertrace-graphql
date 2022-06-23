package org.hypertrace.graphql.spanprocessing.schema.samplingconfigs;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(RateLimitType.TYPE_NAME)
public enum RateLimitType {
  WINDOWED,
  ;
  static final String TYPE_NAME = "SpanProcessingRateLimitType";
}
