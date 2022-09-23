package org.hypertrace.core.graphql.utils.grpc;

import java.util.Optional;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.grpcutils.context.RequestContext;

public interface GrpcContextBuilder {
  RequestContext build(GraphQlRequestContext requestContext);

  Optional<GraphQlRequestContext> tryRestore(RequestContext requestContext);
}
