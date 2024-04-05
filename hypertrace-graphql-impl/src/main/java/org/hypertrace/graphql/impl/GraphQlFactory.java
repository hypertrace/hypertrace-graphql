package org.hypertrace.graphql.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.schema.GraphQLSchema;
import org.hypertrace.core.graphql.context.GraphQlRequestContextBuilder;
import org.hypertrace.core.graphql.spi.config.GraphQlEndpointConfig;
import org.hypertrace.core.graphql.spi.lifecycle.GraphQlServiceLifecycle;
import org.hypertrace.core.grpcutils.client.GrpcChannelRegistry;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;

public class GraphQlFactory {
  public static GraphQLConfiguration build(
      HypertraceGraphQlServiceConfig serviceConfig,
      GraphQlEndpointConfig endpointConfig,
      GraphQlServiceLifecycle serviceLifecycle,
      GrpcChannelRegistry grpcChannelRegistry) {
    final Injector injector =
        Guice.createInjector(
            new GraphQlModule(
                serviceConfig, endpointConfig, serviceLifecycle, grpcChannelRegistry));

    return GraphQLConfiguration.with(injector.getInstance(GraphQLSchema.class))
        .with(injector.getInstance(GraphQlRequestContextBuilder.class))
        .asyncTimeout(endpointConfig.getTimeout().toMillis())
        .build();
  }
}
