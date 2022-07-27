package org.hypertrace.graphql.service;

import java.util.List;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.serviceframework.http.HttpContainerEnvironment;
import org.hypertrace.core.serviceframework.http.HttpHandlerDefinition;
import org.hypertrace.core.serviceframework.http.HttpHandlerDefinition.CorsConfig;
import org.hypertrace.core.serviceframework.http.HttpHandlerFactory;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;
import org.hypertrace.graphql.impl.GraphQlFactory;

public class GraphQlServiceFactory implements HttpHandlerFactory {
  private static final String SERVICE_NAME = "hypertrace-graphql";

  @Override
  public List<HttpHandlerDefinition> buildHandlers(HttpContainerEnvironment environment) {
    HypertraceGraphQlServiceConfig config =
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
