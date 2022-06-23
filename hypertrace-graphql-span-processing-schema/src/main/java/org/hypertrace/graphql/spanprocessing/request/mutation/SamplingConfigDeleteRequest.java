package org.hypertrace.graphql.spanprocessing.request.mutation;

import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface SamplingConfigDeleteRequest {
  GraphQlRequestContext context();

  String id();
}
