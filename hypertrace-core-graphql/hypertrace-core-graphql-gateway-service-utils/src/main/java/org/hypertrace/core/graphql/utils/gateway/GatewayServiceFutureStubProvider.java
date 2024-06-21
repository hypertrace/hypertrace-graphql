package org.hypertrace.core.graphql.utils.gateway;

import io.grpc.CallCredentials;
import javax.inject.Inject;
import javax.inject.Provider;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.gateway.service.GatewayServiceGrpc;
import org.hypertrace.gateway.service.GatewayServiceGrpc.GatewayServiceFutureStub;

class GatewayServiceFutureStubProvider implements Provider<GatewayServiceFutureStub> {

  private final GraphQlServiceConfig serviceConfig;
  private final CallCredentials credentials;
  private final GrpcChannelRegistry channelRegistry;

  @Inject
  GatewayServiceFutureStubProvider(
      GraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GrpcChannelRegistry channelRegistry) {
    this.serviceConfig = serviceConfig;
    this.credentials = credentials;
    this.channelRegistry = channelRegistry;
  }

  @Override
  public GatewayServiceFutureStub get() {
    return GatewayServiceGrpc.newFutureStub(
            channelRegistry.forAddress(
                serviceConfig.getGatewayServiceHost(), serviceConfig.getGatewayServicePort()))
        .withCallCredentials(credentials);
  }
}
