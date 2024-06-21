package org.hypertrace.core.graphql.impl;

import com.google.inject.AbstractModule;
import org.hypertrace.core.graphql.attributes.AttributeStoreModule;
import org.hypertrace.core.graphql.atttributes.scopes.HypertraceCoreAttributeScopeModule;
import org.hypertrace.core.graphql.common.schema.CommonSchemaModule;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeUtilsModule;
import org.hypertrace.core.graphql.context.GraphQlRequestContextModule;
import org.hypertrace.core.graphql.deserialization.GraphQlDeserializationRegistryModule;
import org.hypertrace.core.graphql.log.event.LogEventSchemaModule;
import org.hypertrace.core.graphql.metadata.MetadataSchemaModule;
import org.hypertrace.core.graphql.request.transformation.RequestTransformationModule;
import org.hypertrace.core.graphql.rx.RxUtilModule;
import org.hypertrace.core.graphql.schema.registry.GraphQlSchemaRegistryModule;
import org.hypertrace.core.graphql.span.SpanSchemaModule;
import org.hypertrace.core.graphql.spi.config.GraphQlEndpointConfig;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.spi.lifecycle.GraphQlServiceLifecycle;
import org.hypertrace.core.graphql.trace.TraceSchemaModule;
import org.hypertrace.core.graphql.utils.gateway.GatewayUtilsModule;
import org.hypertrace.core.graphql.utils.grpc.GraphQlGrpcModule;
import org.hypertrace.core.graphql.utils.schema.SchemaUtilsModule;
import org.hypertrace.core.grpcutils.client.GrpcChannelRegistry;

class GraphQlModule extends AbstractModule {

  private final GraphQlServiceConfig serviceConfig;
  private final GraphQlEndpointConfig endpointConfig;
  private final GraphQlServiceLifecycle lifecycle;
  private final GrpcChannelRegistry grpcChannelRegistry;

  public GraphQlModule(
      final GraphQlServiceConfig serviceConfig,
      final GraphQlEndpointConfig endpointConfig,
      final GraphQlServiceLifecycle lifecycle,
      final GrpcChannelRegistry grpcChannelRegistry) {
    this.serviceConfig = serviceConfig;
    this.endpointConfig = endpointConfig;
    this.lifecycle = lifecycle;
    this.grpcChannelRegistry = grpcChannelRegistry;
  }

  @Override
  protected void configure() {
    bind(GraphQlServiceConfig.class).toInstance(this.serviceConfig);
    bind(GraphQlEndpointConfig.class).toInstance(this.endpointConfig);
    bind(GraphQlServiceLifecycle.class).toInstance(this.lifecycle);
    bind(GrpcChannelRegistry.class).toInstance(this.grpcChannelRegistry);
    install(new GraphQlRequestContextModule());
    install(new GraphQlGrpcModule());
    install(new GraphQlSchemaRegistryModule());
    install(new GraphQlDeserializationRegistryModule());
    install(new CommonSchemaModule());
    install(new GatewayUtilsModule());
    install(new SchemaUtilsModule());
    install(new AttributeStoreModule());
    install(new AttributeUtilsModule());
    install(new HypertraceCoreAttributeScopeModule());
    install(new MetadataSchemaModule());
    install(new SpanSchemaModule());
    install(new TraceSchemaModule());
    install(new RxUtilModule());
    install(new LogEventSchemaModule());
    install(new RequestTransformationModule());
  }
}
