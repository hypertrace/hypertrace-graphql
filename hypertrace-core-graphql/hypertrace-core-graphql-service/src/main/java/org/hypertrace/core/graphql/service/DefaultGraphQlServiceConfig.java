package org.hypertrace.core.graphql.service;

import com.typesafe.config.Config;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;

class DefaultGraphQlServiceConfig implements GraphQlServiceConfig {

  private static final Long DEFAULT_CLIENT_TIMEOUT_SECONDS = 10L;

  private static final String SERVICE_NAME_CONFIG = "service.name";
  private static final String SERVICE_PORT_CONFIG = "service.port";

  private static final String DEFAULT_TENANT_ID = "defaultTenantId";

  private static final String ATTRIBUTE_SERVICE_HOST_PROPERTY = "attribute.service.host";
  private static final String ATTRIBUTE_SERVICE_PORT_PROPERTY = "attribute.service.port";
  private static final String ATTRIBUTE_SERVICE_CLIENT_TIMEOUT = "attribute.service.timeout";

  private static final String GATEWAY_SERVICE_HOST_PROPERTY = "gateway.service.host";
  private static final String GATEWAY_SERVICE_PORT_PROPERTY = "gateway.service.port";
  private static final String GATEWAY_SERVICE_CLIENT_TIMEOUT = "gateway.service.timeout";
  private static final String GATEWAY_SERVICE_CLIENT_MAX_INBOUND_MESSAGE_SIZE =
      "gateway.service.maxMessageSize.inbound";

  private final String serviceName;
  private final int servicePort;
  private final Optional<String> defaultTenantId;
  private final String attributeServiceHost;
  private final int attributeServicePort;
  private final Duration attributeServiceTimeout;
  private final String gatewayServiceHost;
  private final int gatewayServicePort;
  private final Duration gatewayServiceTimeout;
  private final int gatewayServiceMaxInboundMessageSize;

  DefaultGraphQlServiceConfig(Config untypedConfig) {
    this.serviceName = untypedConfig.getString(SERVICE_NAME_CONFIG);
    this.servicePort = untypedConfig.getInt(SERVICE_PORT_CONFIG);
    this.defaultTenantId = optionallyGet(() -> untypedConfig.getString(DEFAULT_TENANT_ID));

    this.attributeServiceHost = untypedConfig.getString(ATTRIBUTE_SERVICE_HOST_PROPERTY);
    this.attributeServicePort = untypedConfig.getInt(ATTRIBUTE_SERVICE_PORT_PROPERTY);
    this.attributeServiceTimeout =
        getTimeoutOrFallback(() -> untypedConfig.getDuration(ATTRIBUTE_SERVICE_CLIENT_TIMEOUT));

    this.gatewayServiceHost = untypedConfig.getString(GATEWAY_SERVICE_HOST_PROPERTY);
    this.gatewayServicePort = untypedConfig.getInt(GATEWAY_SERVICE_PORT_PROPERTY);
    this.gatewayServiceTimeout =
        getTimeoutOrFallback(() -> untypedConfig.getDuration(GATEWAY_SERVICE_CLIENT_TIMEOUT));
    this.gatewayServiceMaxInboundMessageSize =
        untypedConfig.getBytes(GATEWAY_SERVICE_CLIENT_MAX_INBOUND_MESSAGE_SIZE).intValue();
  }

  @Override
  public int getServicePort() {
    return servicePort;
  }

  @Override
  public String getServiceName() {
    return serviceName;
  }

  @Override
  public Optional<String> getDefaultTenantId() {
    return this.defaultTenantId;
  }

  @Override
  public String getAttributeServiceHost() {
    return this.attributeServiceHost;
  }

  @Override
  public int getAttributeServicePort() {
    return this.attributeServicePort;
  }

  @Override
  public Duration getAttributeServiceTimeout() {
    return attributeServiceTimeout;
  }

  @Override
  public String getGatewayServiceHost() {
    return this.gatewayServiceHost;
  }

  @Override
  public int getGatewayServicePort() {
    return this.gatewayServicePort;
  }

  @Override
  public Duration getGatewayServiceTimeout() {
    return gatewayServiceTimeout;
  }

  @Override
  public int getGatewayServiceMaxInboundMessageSize() {
    return this.gatewayServiceMaxInboundMessageSize;
  }

  private Duration getTimeoutOrFallback(Supplier<Duration> durationSupplier) {
    return optionallyGet(durationSupplier)
        .orElse(Duration.ofSeconds(DEFAULT_CLIENT_TIMEOUT_SECONDS));
  }

  private <T> Optional<T> optionallyGet(Supplier<T> valueSupplier) {
    try {
      return Optional.ofNullable(valueSupplier.get());
    } catch (Throwable unused) {
      return Optional.empty();
    }
  }
}
