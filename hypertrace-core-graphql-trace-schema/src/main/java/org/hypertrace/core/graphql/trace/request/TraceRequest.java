package org.hypertrace.core.graphql.trace.request;

import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.trace.schema.arguments.TraceType;

public interface TraceRequest extends ContextualRequest {
  ResultSetRequest<OrderArgument> resultSetRequest();

  TraceType traceType();
}
