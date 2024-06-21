package org.hypertrace.core.graphql.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.schema.GraphQLSchema;
import org.hypertrace.core.graphql.context.GraphQlRequestContextBuilder;
import org.hypertrace.core.graphql.spi.config.GraphQlEndpointConfig;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.spi.lifecycle.GraphQlServiceLifecycle;
import org.hypertrace.core.grpcutils.client.GrpcChannelRegistry;

public class GraphQlFactory {
  public static GraphQLConfiguration build(
      GraphQlServiceConfig serviceConfig,
      GraphQlEndpointConfig endpointConfig,
      GraphQlServiceLifecycle lifecycle,
      GrpcChannelRegistry grpcChannelRegistry) {
    final Injector injector =
        Guice.createInjector(
            new GraphQlModule(serviceConfig, endpointConfig, lifecycle, grpcChannelRegistry));

    return GraphQLConfiguration.with(injector.getInstance(GraphQLSchema.class))
        .with(injector.getInstance(GraphQlRequestContextBuilder.class))
        .asyncTimeout(endpointConfig.getTimeout().toMillis())
        .build();
  }
}
