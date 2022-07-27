package org.hypertrace.core.graphql.service;

import java.util.List;
import org.hypertrace.core.graphql.impl.GraphQlFactory;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.serviceframework.config.ConfigClient;
import org.hypertrace.core.serviceframework.http.HttpContainerEnvironment;
import org.hypertrace.core.serviceframework.http.HttpHandlerDefinition;
import org.hypertrace.core.serviceframework.http.HttpHandlerDefinition.CorsConfig;
import org.hypertrace.core.serviceframework.http.HttpHandlerFactory;
import org.hypertrace.core.serviceframework.http.StandAloneHttpPlatformServiceContainer;

public class GraphQlService extends StandAloneHttpPlatformServiceContainer {
  private static final String SERVICE_NAME = "hypertrace-core-graphql";

  public GraphQlService(ConfigClient configClient) {
    super(configClient);
  }

  @Override
  protected List<HttpHandlerFactory> getHandlerFactories() {
    return List.of(this::buildHandlerDefinition);
  }

  List<HttpHandlerDefinition> buildHandlerDefinition(HttpContainerEnvironment environment) {
    GraphQlServiceConfig config =
        new DefaultGraphQlServiceConfig(environment.getConfig(SERVICE_NAME));
    DefaultGraphQlServiceLifecycle serviceLifecycle = new DefaultGraphQlServiceLifecycle();
    environment.getLifecycle().shutdownComplete().thenRun(serviceLifecycle::shutdown);

    return List.of(
        HttpHandlerDefinition.builder()
            .name("graphql")
            .port(config.getServicePort())
            .contextPath(config.getGraphQlUrlPath())
            .corsConfig(buildCorsConfig(config))
            .servlet(new GraphQlServiceHttpServlet(GraphQlFactory.build(config, serviceLifecycle)))
            .build());
  }

  private CorsConfig buildCorsConfig(GraphQlServiceConfig config) {
    if (!config.isCorsEnabled()) {
      return null;
    }
    return CorsConfig.builder().allowedHeaders(List.of("*")).build();
  }
}
