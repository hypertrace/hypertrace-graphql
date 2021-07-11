package org.hypertrace.graphql.config;

import java.time.Duration;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;

public interface HypertraceGraphQlServiceConfig extends GraphQlServiceConfig {
  String getEntityServiceHost();

  int getEntityServicePort();

  Duration getEntityServiceTimeout();

  String getConfigServiceHost();

  int getConfigServicePort();

  Duration getConfigServiceTimeout();
}
