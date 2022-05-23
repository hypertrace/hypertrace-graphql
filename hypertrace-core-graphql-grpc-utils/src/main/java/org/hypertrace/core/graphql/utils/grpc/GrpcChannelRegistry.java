package org.hypertrace.core.graphql.utils.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import org.hypertrace.core.grpcutils.client.GrpcChannelConfig;

public interface GrpcChannelRegistry {
  Channel forAddress(String host, int port);

  ManagedChannel forAddress(String host, int port, GrpcChannelConfig channelConfig);
}
