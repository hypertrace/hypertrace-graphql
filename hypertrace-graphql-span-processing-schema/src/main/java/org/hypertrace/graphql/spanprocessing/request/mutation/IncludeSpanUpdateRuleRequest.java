package org.hypertrace.graphql.spanprocessing.request.mutation;

import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.spanprocessing.schema.mutation.IncludeSpanRuleUpdate;

public interface IncludeSpanUpdateRuleRequest {
  GraphQlRequestContext context();

  IncludeSpanRuleUpdate updateInput();
}
