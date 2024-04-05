package org.hypertrace.core.graphql.service;

import com.typesafe.config.Config;
import java.util.List;
import org.hypertrace.core.graphql.impl.GraphQlFactory;
import org.hypertrace.core.graphql.spi.config.GraphQlEndpointConfig;
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
    Config rawConfig = environment.getConfig(SERVICE_NAME);
    GraphQlServiceConfig serviceConfig = new DefaultGraphQlServiceConfig(rawConfig);
    GraphQlEndpointConfig endpointConfig = DefaultGraphQlEndpointConfig.fromConfig(rawConfig);
    DefaultGraphQlServiceLifecycle serviceLifecycle = new DefaultGraphQlServiceLifecycle();
    environment.getLifecycle().shutdownComplete().thenRun(serviceLifecycle::shutdown);

    return List.of(
        HttpHandlerDefinition.builder()
            .name("graphql")
            .port(serviceConfig.getServicePort())
            .contextPath(endpointConfig.getUrlPath())
            .corsConfig(buildCorsConfig(endpointConfig))
            .servlet(
                new GraphQlServiceHttpServlet(
                    GraphQlFactory.build(
                        serviceConfig,
                        endpointConfig,
                        serviceLifecycle,
                        environment.getChannelRegistry())))
            .build());
  }

  private CorsConfig buildCorsConfig(GraphQlEndpointConfig config) {
    if (!config.isCorsEnabled()) {
      return null;
    }
    return CorsConfig.builder().allowedHeaders(List.of("*")).build();
  }
}
