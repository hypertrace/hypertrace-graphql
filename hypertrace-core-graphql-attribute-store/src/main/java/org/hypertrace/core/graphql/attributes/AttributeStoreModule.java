package org.hypertrace.core.graphql.attributes;

import com.google.inject.AbstractModule;
import io.grpc.CallCredentials;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GraphQlGrpcContextBuilder;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;

public class AttributeStoreModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(AttributeStore.class).to(CachingAttributeStore.class);
    requireBinding(GraphQlServiceConfig.class);
    requireBinding(GraphQlGrpcContextBuilder.class);
    requireBinding(CallCredentials.class);
    requireBinding(GrpcChannelRegistry.class);
  }
}
