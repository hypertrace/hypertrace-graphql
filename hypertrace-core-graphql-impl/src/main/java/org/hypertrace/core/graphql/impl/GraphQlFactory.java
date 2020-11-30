package org.hypertrace.core.graphql.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.schema.GraphQLSchema;
import org.hypertrace.core.graphql.context.GraphQlRequestContextBuilder;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.spi.lifecycle.GraphQlServiceLifecycle;

public class GraphQlFactory {
  public static GraphQLConfiguration build(
      GraphQlServiceConfig config, GraphQlServiceLifecycle lifecycle) {
    final Injector injector = Guice.createInjector(new GraphQlModule(config, lifecycle));

    return GraphQLConfiguration.with(injector.getInstance(GraphQLSchema.class))
        .with(config.isAsyncServlet())
        .asyncTimeout(30000) // https://github.com/graphql-java-kickstart/graphql-java-servlet/issues/282
        .with(injector.getInstance(GraphQlRequestContextBuilder.class))
        .build();
  }
}
