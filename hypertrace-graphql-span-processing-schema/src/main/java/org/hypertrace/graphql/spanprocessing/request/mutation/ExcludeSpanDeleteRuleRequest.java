package org.hypertrace.graphql.spanprocessing.request.mutation;

import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface ExcludeSpanDeleteRuleRequest {
  GraphQlRequestContext context();

  String id();
}
