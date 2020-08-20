package org.hypertrace.graphql.service;

import org.hypertrace.core.serviceframework.PlatformService;
import org.hypertrace.core.serviceframework.config.ConfigClient;

public class GraphQlService extends PlatformService {

  private GraphQlServiceImpl graphQlServiceImpl;

  public GraphQlService(ConfigClient configClient) {
    super(configClient);
  }

  @Override
  protected void doInit() {
    graphQlServiceImpl = new GraphQlServiceImpl(this.getAppConfig());
  }

  @Override
  protected void doStart() { graphQlServiceImpl.start(); }

  @Override
  protected void doStop() { graphQlServiceImpl.stop(); }

  @Override
  public boolean healthCheck() {
    return true;
  }

  @Override
  public String getServiceName() {
    return graphQlServiceImpl.getServiceName();
  }
}
