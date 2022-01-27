package org.hypertrace.core.graphql.spi.config;

import java.time.Duration;
import java.util.Optional;

public interface GraphQlServiceConfig {

  int getServicePort();

  String getServiceName();

  String getGraphQlUrlPath();

  boolean isCorsEnabled();

  Duration getGraphQlTimeout();

  Optional<String> getDefaultTenantId();

  int getMaxIoThreads();

  String getAttributeServiceHost();

  int getAttributeServicePort();

  Duration getAttributeServiceTimeout();

  String getGatewayServiceHost();

  int getGatewayServicePort();

  Duration getGatewayServiceTimeout();
}
