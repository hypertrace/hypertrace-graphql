package org.hypertrace.graphql.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.schema.GraphQLSchema;
import org.hypertrace.core.graphql.context.GraphQlRequestContextBuilder;
import org.hypertrace.core.graphql.spi.lifecycle.GraphQlServiceLifecycle;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;

public class GraphQlFactory {
  public static GraphQLConfiguration build(
      HypertraceGraphQlServiceConfig config, GraphQlServiceLifecycle serviceLifecycle) {
    final Injector injector = Guice.createInjector(new GraphQlModule(config, serviceLifecycle));

    return GraphQLConfiguration.with(injector.getInstance(GraphQLSchema.class))
        .with(injector.getInstance(GraphQlRequestContextBuilder.class))
        .build();
  }
}
