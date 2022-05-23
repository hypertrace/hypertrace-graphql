package org.hypertrace.core.graphql.utils.grpc;

import io.grpc.ManagedChannel;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hypertrace.core.graphql.spi.lifecycle.GraphQlServiceLifecycle;
import org.hypertrace.core.grpcutils.client.GrpcChannelConfig;

@Singleton
class DefaultGrpcChannelRegistry implements GrpcChannelRegistry {

  private final org.hypertrace.core.grpcutils.client.GrpcChannelRegistry delegate =
      new org.hypertrace.core.grpcutils.client.GrpcChannelRegistry();

  @Inject
  DefaultGrpcChannelRegistry(GraphQlServiceLifecycle serviceLifecycle) {
    serviceLifecycle.shutdownCompletion().thenRun(this.delegate::shutdown);
  }

  @Override
  public ManagedChannel forAddress(String host, int port) {
    return this.delegate.forPlaintextAddress(host, port);
  }

  @Override
  public ManagedChannel forAddress(String host, int port, GrpcChannelConfig channelConfig) {
    return this.delegate.forPlaintextAddress(host, port, channelConfig);
  }
}
