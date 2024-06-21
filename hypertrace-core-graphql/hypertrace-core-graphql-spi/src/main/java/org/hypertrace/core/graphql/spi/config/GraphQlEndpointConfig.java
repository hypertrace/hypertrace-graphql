package org.hypertrace.core.graphql.spi.config;

/** This specifies the configuration of one graphql endpoint in the server */
public interface GraphQlEndpointConfig {

  boolean isIntrospectionAllowed();

  boolean isCorsEnabled();

  java.time.Duration getTimeout();

  String getUrlPath();

  int getMaxRequestThreads();

  int getMaxIoThreads();
}
