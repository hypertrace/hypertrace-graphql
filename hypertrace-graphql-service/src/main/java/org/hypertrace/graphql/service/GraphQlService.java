package org.hypertrace.graphql.service;

import java.util.List;
import org.hypertrace.core.serviceframework.config.ConfigClient;
import org.hypertrace.core.serviceframework.http.HttpHandlerFactory;
import org.hypertrace.core.serviceframework.http.StandAloneHttpPlatformServiceContainer;

public class GraphQlService extends StandAloneHttpPlatformServiceContainer {

  public GraphQlService(ConfigClient configClient) {
    super(configClient);
  }

  @Override
  protected List<HttpHandlerFactory> getHandlerFactories() {
    return List.of(new GraphQlServiceFactory());
  }
}
