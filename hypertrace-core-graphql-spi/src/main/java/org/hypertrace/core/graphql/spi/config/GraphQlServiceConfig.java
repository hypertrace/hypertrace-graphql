package org.hypertrace.core.graphql.spi.config;

import java.util.Optional;

public interface GraphQlServiceConfig {

  int getServicePort();

  String getServiceName();

  String getGraphqlUrlPath();

  boolean isCorsEnabled();

  Optional<String> getDefaultTenantId();

  int getMaxIoThreads();

  String getAttributeServiceHost();

  int getAttributeServicePort();

  String getGatewayServiceHost();

  int getGatewayServicePort();
}
