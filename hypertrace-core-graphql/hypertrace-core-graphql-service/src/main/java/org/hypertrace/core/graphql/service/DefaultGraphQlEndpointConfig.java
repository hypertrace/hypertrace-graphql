package org.hypertrace.core.graphql.service;

import com.typesafe.config.Config;
import java.time.Duration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.hypertrace.core.graphql.spi.config.GraphQlEndpointConfig;

@Value
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
class DefaultGraphQlEndpointConfig implements GraphQlEndpointConfig {
  private static final String URL_PATH_PROP_KEY = "graphql.urlPath";
  private static final String TIMEOUT_PROP_KEY = "graphql.timeout";
  private static final String MAX_IO_THREADS_PROP_KEY = "threads.io.max";
  private static final String MAX_REQUEST_THREADS_PROP_KEY = "threads.request.max";
  private static final String CORS_ENABLED_PROP_KEY = "graphql.corsEnabled";
  private static final String INTROSPECTION_ENABLED_PROP_KEY = "introspection.enabled";

  String urlPath;
  Duration timeout;
  int maxRequestThreads;
  int maxIoThreads;
  boolean corsEnabled;
  boolean introspectionAllowed;

  static GraphQlEndpointConfig fromConfig(Config config) {
    return new DefaultGraphQlEndpointConfigBuilder()
        .urlPath(config.getString(URL_PATH_PROP_KEY))
        .timeout(config.getDuration(TIMEOUT_PROP_KEY))
        .maxRequestThreads(config.getInt(MAX_REQUEST_THREADS_PROP_KEY))
        .maxIoThreads(config.getInt(MAX_IO_THREADS_PROP_KEY))
        .corsEnabled(config.getBoolean(CORS_ENABLED_PROP_KEY))
        .introspectionAllowed(config.getBoolean(INTROSPECTION_ENABLED_PROP_KEY))
        .build();
  }
}
