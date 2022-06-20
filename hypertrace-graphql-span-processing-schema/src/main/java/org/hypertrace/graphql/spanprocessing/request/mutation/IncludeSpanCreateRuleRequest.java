package org.hypertrace.graphql.spanprocessing.request.mutation;

import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.spanprocessing.schema.mutation.IncludeSpanRuleCreate;

public interface IncludeSpanCreateRuleRequest {
  GraphQlRequestContext context();

  IncludeSpanRuleCreate createInput();
}
