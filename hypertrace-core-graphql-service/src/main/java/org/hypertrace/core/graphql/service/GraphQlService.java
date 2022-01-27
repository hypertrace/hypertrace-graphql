package org.hypertrace.core.graphql.service;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.hypertrace.core.graphql.impl.GraphQlFactory;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.serviceframework.PlatformService;
import org.hypertrace.core.serviceframework.config.ConfigClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphQlService extends PlatformService {

  private static final Logger LOG = LoggerFactory.getLogger(GraphQlService.class);

  private GraphQlServiceConfig graphQlServiceConfig;
  private DefaultGraphQlServiceLifecycle serviceLifecycle;
  private Server server;

  public GraphQlService(ConfigClient configClient) {
    super(configClient);
  }

  @Override
  protected void doInit() {
    this.graphQlServiceConfig = new DefaultGraphQlServiceConfig(this.getAppConfig());
    this.serviceLifecycle = new DefaultGraphQlServiceLifecycle();
    this.server = new Server(this.graphQlServiceConfig.getServicePort());

    ServletContextHandler context = new ServletContextHandler();
    if (this.graphQlServiceConfig.isCorsEnabled()) {
      context.addFilter(
          CrossOriginFilter.class,
          this.graphQlServiceConfig.getGraphQlUrlPath(),
          EnumSet.of(DispatcherType.REQUEST));
    }

    context.addServlet(
        new ServletHolder(
            new GraphQlServiceHttpServlet(
                GraphQlFactory.build(this.graphQlServiceConfig, this.serviceLifecycle))),
        this.graphQlServiceConfig.getGraphQlUrlPath());

    this.server.setHandler(context);
    this.server.setStopAtShutdown(true);
  }

  @Override
  protected void doStart() {
    LOG.info("Starting service: {}", this.getServiceName());

    try {
      server.start();
    } catch (Exception e) {
      LOG.error("Failed to start service: {}", this.getServiceName());
      throw new RuntimeException(e);
    }

    try {
      server.join();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(ie);
    }
  }

  @Override
  protected void doStop() {
    LOG.info("Shutting down service: {}", this.getServiceName());
    if (this.serviceLifecycle != null) {
      this.serviceLifecycle.shutdown();
    }
    while (!server.isStopped()) {
      try {
        server.stop();
      } catch (Exception e) {
        LOG.error("Failed to shutdown service: {}", this.getServiceName());
        throw new RuntimeException(e);
      }
    }
    try {
      Thread.sleep(100);
    } catch (InterruptedException ignore) {
    }
  }

  @Override
  public boolean healthCheck() {
    return true;
  }
}
