package org.hypertrace.core.graphql.spi.config;

import java.util.Optional;

public interface GraphQlServiceConfig {

  int getServicePort();

  String getServiceName();

  String getGraphqlUrlPath();

  boolean isAsyncServlet();

  boolean isCorsEnabled();

  Optional<String> getDefaultTenantId();

  String getAttributeServiceHost();

  int getAttributeServicePort();

  String getGatewayServiceHost();

  int getGatewayServicePort();
}
