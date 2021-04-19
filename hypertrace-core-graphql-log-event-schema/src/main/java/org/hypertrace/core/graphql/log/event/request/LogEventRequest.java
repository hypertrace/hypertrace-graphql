package org.hypertrace.core.graphql.log.event.request;

import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface LogEventRequest {
  GraphQlRequestContext requestContext();
}
