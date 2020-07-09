package org.hypertrace.core.graphql.utils.grpc;

import java.util.Map;
import javax.annotation.Nonnull;
import org.hypertrace.core.grpcutils.context.RequestContext;

class PlatformRequestContextBuilder {

  RequestContext build(@Nonnull Map<String, String> headers) {
    RequestContext platformContext = new RequestContext();
    headers.forEach(platformContext::add);
    return platformContext;
  }
}
