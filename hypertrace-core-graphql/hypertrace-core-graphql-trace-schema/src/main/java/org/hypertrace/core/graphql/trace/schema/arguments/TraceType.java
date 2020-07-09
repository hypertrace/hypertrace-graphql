package org.hypertrace.core.graphql.trace.schema.arguments;

import graphql.annotations.annotationTypes.GraphQLName;

// TODO temporary for backwards compatibility
@GraphQLName(TraceType.TYPE_NAME)
public enum TraceType {
  TRACE,
  API_TRACE,
  BACKEND_TRACE;

  static final String TYPE_NAME = "TraceType";
}
