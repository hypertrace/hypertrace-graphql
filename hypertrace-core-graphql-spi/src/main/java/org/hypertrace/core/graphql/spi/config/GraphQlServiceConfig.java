package org.hypertrace.core.graphql.spi.config;

import java.time.Duration;
import java.util.Optional;

public interface GraphQlServiceConfig {

  int getServicePort();

  String getServiceName();

  Optional<String> getDefaultTenantId();

  String getAttributeServiceHost();

  int getAttributeServicePort();

  Duration getAttributeServiceTimeout();

  String getGatewayServiceHost();

  int getGatewayServicePort();

  Duration getGatewayServiceTimeout();

  int getGatewayServiceMaxInboundMessageSize();
}
