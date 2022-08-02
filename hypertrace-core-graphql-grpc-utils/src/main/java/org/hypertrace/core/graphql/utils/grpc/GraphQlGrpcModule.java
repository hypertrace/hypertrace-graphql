package org.hypertrace.core.graphql.utils.grpc;

import static org.hypertrace.core.grpcutils.client.RequestContextClientCallCredsProviderFactory.getClientCallCredsProvider;

import com.google.inject.AbstractModule;
import io.grpc.CallCredentials;

public class GraphQlGrpcModule extends AbstractModule {

  private final org.hypertrace.core.grpcutils.client.GrpcChannelRegistry channelRegistry;

  public GraphQlGrpcModule(
      org.hypertrace.core.grpcutils.client.GrpcChannelRegistry channelRegistry) {
    this.channelRegistry = channelRegistry;
  }

  @Override
  protected void configure() {
    bind(CallCredentials.class).toInstance(getClientCallCredsProvider().get());
    bind(GraphQlGrpcContextBuilder.class).to(DefaultGraphQlGrpcContextBuilder.class);
    bind(GrpcChannelRegistry.class)
        .toInstance(new DefaultGrpcChannelRegistry(this.channelRegistry));
    bind(GrpcContextBuilder.class).to(PlatformGrpcContextBuilder.class);
  }
}
