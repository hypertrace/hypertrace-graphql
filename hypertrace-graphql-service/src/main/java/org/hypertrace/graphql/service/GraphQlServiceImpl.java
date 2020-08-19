package org.hypertrace.graphql.service;

import com.typesafe.config.Config;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.graphql.impl.GraphQlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphQlServiceImpl {
  private static final Logger LOG = LoggerFactory.getLogger(GraphQlServiceImpl.class);

  private GraphQlServiceConfig graphQlServiceConfig;
  private DefaultGraphQlServiceLifecycle serviceLifecycle;
  private Server server;

  public GraphQlServiceImpl(Config appConfig) {
    this.graphQlServiceConfig = new DefaultGraphQlServiceConfig(appConfig);
    this.serviceLifecycle = new DefaultGraphQlServiceLifecycle();
    this.server = new Server(this.graphQlServiceConfig.getServicePort());

    ServletContextHandler context = new ServletContextHandler();
    if (this.graphQlServiceConfig.isCorsEnabled()) {
      context.addFilter(
              CrossOriginFilter.class,
              this.graphQlServiceConfig.getGraphqlUrlPath(),
              EnumSet.of(DispatcherType.REQUEST));
    }

    context.addServlet(
            new ServletHolder(
                    new GraphQlServiceHttpServlet(
                            GraphQlFactory.build(this.graphQlServiceConfig, this.serviceLifecycle))),
            this.graphQlServiceConfig.getGraphqlUrlPath());

    this.server.setHandler(context);
    this.server.setStopAtShutdown(true);
  }

  public void start() {
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

  public void stop() {
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

  String getServiceName() {
    return this.graphQlServiceConfig.getServiceName();
  }
}
