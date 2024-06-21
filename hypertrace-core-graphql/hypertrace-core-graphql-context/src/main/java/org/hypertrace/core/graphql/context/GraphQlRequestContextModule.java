package org.hypertrace.core.graphql.context;

import com.google.inject.AbstractModule;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;

public class GraphQlRequestContextModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(GraphQlRequestContextBuilder.class).to(DefaultGraphQlRequestContextBuilder.class);
    requireBinding(GraphQlServiceConfig.class);
  }
}
