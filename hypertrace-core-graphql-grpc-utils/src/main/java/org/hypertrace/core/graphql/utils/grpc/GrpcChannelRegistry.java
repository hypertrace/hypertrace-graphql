package org.hypertrace.core.graphql.utils.grpc;

import io.grpc.Channel;

public interface GrpcChannelRegistry {
  Channel forAddress(String host, int port);
}
