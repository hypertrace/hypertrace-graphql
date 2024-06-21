package org.hypertrace.core.graphql.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import com.google.inject.Guice;
import graphql.schema.GraphQLSchema;
import org.hypertrace.core.graphql.spi.config.GraphQlEndpointConfig;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.spi.lifecycle.GraphQlServiceLifecycle;
import org.hypertrace.core.grpcutils.client.GrpcChannelRegistry;
import org.junit.jupiter.api.Test;

public class GraphQlModuleTest {

  @Test
  public void testResolveBindings() {
    assertDoesNotThrow(
        () ->
            Guice.createInjector(
                    new GraphQlModule(
                        mock(GraphQlServiceConfig.class),
                        mock(GraphQlEndpointConfig.class),
                        mock(GraphQlServiceLifecycle.class),
                        mock(GrpcChannelRegistry.class)))
                .getAllBindings());
  }

  @Test
  public void testResolveSchema() {
    assertDoesNotThrow(
        () ->
            Guice.createInjector(
                    new GraphQlModule(
                        mock(GraphQlServiceConfig.class),
                        mock(GraphQlEndpointConfig.class),
                        mock(GraphQlServiceLifecycle.class),
                        mock(GrpcChannelRegistry.class)))
                .getInstance(GraphQLSchema.class));
  }
}
