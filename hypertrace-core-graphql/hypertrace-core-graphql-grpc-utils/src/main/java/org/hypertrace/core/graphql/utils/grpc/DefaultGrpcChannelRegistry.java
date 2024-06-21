package org.hypertrace.core.graphql.utils.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import javax.inject.Inject;
import org.hypertrace.core.grpcutils.client.GrpcChannelConfig;

class DefaultGrpcChannelRegistry implements GrpcChannelRegistry {

  private final org.hypertrace.core.grpcutils.client.GrpcChannelRegistry delegate;

  @Inject
  DefaultGrpcChannelRegistry(org.hypertrace.core.grpcutils.client.GrpcChannelRegistry delegate) {
    this.delegate = delegate;
  }

  @Override
  public ManagedChannel forAddress(String host, int port) {
    return this.delegate.forPlaintextAddress(host, port);
  }

  @Override
  public Channel forAddress(String host, int port, GrpcChannelConfig channelConfig) {
    return this.delegate.forPlaintextAddress(host, port, channelConfig);
  }
}
