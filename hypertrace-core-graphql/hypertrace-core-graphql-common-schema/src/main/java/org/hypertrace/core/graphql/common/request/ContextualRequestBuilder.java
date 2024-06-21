package org.hypertrace.core.graphql.common.request;

import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface ContextualRequestBuilder {
  ContextualRequest build(GraphQlRequestContext context);
}
