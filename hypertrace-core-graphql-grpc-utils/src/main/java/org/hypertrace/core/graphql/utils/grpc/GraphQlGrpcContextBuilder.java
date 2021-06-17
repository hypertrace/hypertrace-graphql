package org.hypertrace.core.graphql.utils.grpc;

import org.hypertrace.core.graphql.context.GraphQlRequestContext;

/** @deprecated Use {@link GrpcContextBuilder#build(GraphQlRequestContext)} instead */
@Deprecated
public interface GraphQlGrpcContextBuilder {
  GraphQlGrpcContext build(GraphQlRequestContext requestContext);
}
