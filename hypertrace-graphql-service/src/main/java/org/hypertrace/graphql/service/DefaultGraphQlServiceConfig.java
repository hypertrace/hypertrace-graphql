package org.hypertrace.graphql.service;

import com.typesafe.config.Config;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Value;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;

@Value
class DefaultGraphQlServiceConfig implements HypertraceGraphQlServiceConfig {

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

  private static final String ENTITY_SERVICE_HOST_PROPERTY = "entity.service.host";
  private static final String ENTITY_SERVICE_PORT_PROPERTY = "entity.service.port";
  private static final String ENTITY_SERVICE_CLIENT_TIMEOUT = "entity.service.timeout";

  private static final String CONFIG_SERVICE_HOST_PROPERTY = "config.service.host";
  private static final String CONFIG_SERVICE_PORT_PROPERTY = "config.service.port";
  private static final String CONFIG_SERVICE_CLIENT_TIMEOUT = "config.service.timeout";

  String serviceName;
  int servicePort;
  Optional<String> defaultTenantId;
  String attributeServiceHost;
  int attributeServicePort;
  Duration attributeServiceTimeout;
  String gatewayServiceHost;
  int gatewayServicePort;
  Duration gatewayServiceTimeout;
  int gatewayServiceMaxInboundMessageSize;
  String entityServiceHost;
  int entityServicePort;
  Duration entityServiceTimeout;
  String configServiceHost;
  int configServicePort;
  Duration configServiceTimeout;

  DefaultGraphQlServiceConfig(Config untypedConfig) {
    this.serviceName = untypedConfig.getString(SERVICE_NAME_CONFIG);
    this.servicePort = untypedConfig.getInt(SERVICE_PORT_CONFIG);
    this.defaultTenantId = optionallyGet(() -> untypedConfig.getString(DEFAULT_TENANT_ID));

    this.attributeServiceHost = untypedConfig.getString(ATTRIBUTE_SERVICE_HOST_PROPERTY);
    this.attributeServicePort = untypedConfig.getInt(ATTRIBUTE_SERVICE_PORT_PROPERTY);
    this.attributeServiceTimeout =
        getSuppliedDurationOrFallback(
            () -> untypedConfig.getDuration(ATTRIBUTE_SERVICE_CLIENT_TIMEOUT));

    this.gatewayServiceHost = untypedConfig.getString(GATEWAY_SERVICE_HOST_PROPERTY);
    this.gatewayServicePort = untypedConfig.getInt(GATEWAY_SERVICE_PORT_PROPERTY);
    this.gatewayServiceTimeout =
        getSuppliedDurationOrFallback(
            () -> untypedConfig.getDuration(GATEWAY_SERVICE_CLIENT_TIMEOUT));
    this.gatewayServiceMaxInboundMessageSize =
        untypedConfig.getBytes(GATEWAY_SERVICE_CLIENT_MAX_INBOUND_MESSAGE_SIZE).intValue();

    this.entityServiceHost = untypedConfig.getString(ENTITY_SERVICE_HOST_PROPERTY);
    this.entityServicePort = untypedConfig.getInt(ENTITY_SERVICE_PORT_PROPERTY);
    this.entityServiceTimeout =
        getSuppliedDurationOrFallback(
            () -> untypedConfig.getDuration(ENTITY_SERVICE_CLIENT_TIMEOUT));

    this.configServiceHost = untypedConfig.getString(CONFIG_SERVICE_HOST_PROPERTY);
    this.configServicePort = untypedConfig.getInt(CONFIG_SERVICE_PORT_PROPERTY);
    this.configServiceTimeout =
        getSuppliedDurationOrFallback(
            () -> untypedConfig.getDuration(CONFIG_SERVICE_CLIENT_TIMEOUT));
  }

  private Duration getSuppliedDurationOrFallback(Supplier<Duration> durationSupplier) {
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
