package org.hypertrace.core.graphql.utils.grpc;

import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface GraphQlGrpcContextBuilder {
  GraphQlGrpcContext build(GraphQlRequestContext requestContext);
}
