package org.hypertrace.graphql.impl;

import com.google.inject.AbstractModule;
import java.time.Clock;
import org.hypertrace.core.graphql.attributes.AttributeStoreModule;
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
import org.hypertrace.graphql.atttribute.scopes.HypertraceAttributeScopeModule;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;
import org.hypertrace.graphql.entity.EntityIdModule;
import org.hypertrace.graphql.entity.EntitySchemaModule;
import org.hypertrace.graphql.entity.HypertraceEntityModule;
import org.hypertrace.graphql.explorer.ExplorerSchemaModule;
import org.hypertrace.graphql.explorer.context.HypertraceExplorerContextModule;
import org.hypertrace.graphql.label.LabelSchemaModule;
import org.hypertrace.graphql.metric.MetricModule;
import org.hypertrace.graphql.spaces.SpacesSchemaModule;
import org.hypertrace.graphql.spanprocessing.SpanProcessingSchemaModule;
import org.hypertrace.graphql.utils.metrics.gateway.GatewayMetricUtilsModule;

class GraphQlModule extends AbstractModule {

  private final HypertraceGraphQlServiceConfig serviceConfig;
  private final GraphQlEndpointConfig endpointConfig;
  private final GraphQlServiceLifecycle serviceLifecycle;

  private final GrpcChannelRegistry grpcChannelRegistry;

  public GraphQlModule(
      final HypertraceGraphQlServiceConfig serviceConfig,
      final GraphQlEndpointConfig endpointConfig,
      final GraphQlServiceLifecycle serviceLifecycle,
      final GrpcChannelRegistry grpcChannelRegistry) {
    this.serviceConfig = serviceConfig;
    this.endpointConfig = endpointConfig;
    this.serviceLifecycle = serviceLifecycle;
    this.grpcChannelRegistry = grpcChannelRegistry;
  }

  @Override
  protected void configure() {
    bind(GraphQlServiceConfig.class).toInstance(this.serviceConfig);
    bind(HypertraceGraphQlServiceConfig.class).toInstance(this.serviceConfig);
    bind(GraphQlEndpointConfig.class).toInstance(this.endpointConfig);
    bind(GraphQlServiceLifecycle.class).toInstance(this.serviceLifecycle);
    bind(GrpcChannelRegistry.class).toInstance(this.grpcChannelRegistry);
    bind(Clock.class).toInstance(Clock.systemUTC());
    install(new GraphQlRequestContextModule());
    install(new GraphQlGrpcModule());
    install(new GraphQlSchemaRegistryModule());
    install(new GraphQlDeserializationRegistryModule());
    install(new HypertraceAttributeScopeModule());
    install(new CommonSchemaModule());
    install(new MetricModule());
    install(new GatewayUtilsModule());
    install(new GatewayMetricUtilsModule());
    install(new SchemaUtilsModule());
    install(new AttributeStoreModule());
    install(new AttributeUtilsModule());
    install(new MetadataSchemaModule());
    install(new SpanSchemaModule());
    install(new LogEventSchemaModule());
    install(new TraceSchemaModule());
    install(new EntitySchemaModule());
    install(new ExplorerSchemaModule());
    install(new HypertraceExplorerContextModule());
    install(new HypertraceEntityModule());
    install(new RxUtilModule());
    install(new EntityIdModule());
    install(new SpacesSchemaModule());
    install(new RequestTransformationModule());
    install(new LabelSchemaModule());
    install(new SpanProcessingSchemaModule());
  }
}
