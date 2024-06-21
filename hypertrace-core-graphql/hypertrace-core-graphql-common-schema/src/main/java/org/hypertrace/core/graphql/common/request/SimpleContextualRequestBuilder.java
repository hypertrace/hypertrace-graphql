package org.hypertrace.core.graphql.common.request;

import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

class SimpleContextualRequestBuilder implements ContextualRequestBuilder {

  @Override
  public ContextualRequest build(GraphQlRequestContext context) {
    return new SimpleContextualRequest(context);
  }

  @Value
  @Accessors(fluent = true)
  private static class SimpleContextualRequest implements ContextualRequest {
    GraphQlRequestContext context;
  }
}
