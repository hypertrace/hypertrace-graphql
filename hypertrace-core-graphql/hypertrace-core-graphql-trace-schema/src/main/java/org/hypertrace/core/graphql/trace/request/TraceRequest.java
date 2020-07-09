package org.hypertrace.core.graphql.trace.request;

import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.trace.schema.arguments.TraceType;

public interface TraceRequest {
  GraphQlRequestContext context();

  ResultSetRequest<OrderArgument> resultSetRequest();

  TraceType traceType();
}
