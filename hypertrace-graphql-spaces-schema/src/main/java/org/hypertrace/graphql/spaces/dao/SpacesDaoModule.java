package org.hypertrace.graphql.spaces.dao;

import com.google.inject.AbstractModule;
import io.grpc.CallCredentials;
import org.hypertrace.core.graphql.context.GraphQlRequestContextBuilder;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;

public class SpacesDaoModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(SpacesConfigDao.class).to(ConfigServiceSpacesConfigDao.class);
    requireBinding(CallCredentials.class);
    requireBinding(HypertraceGraphQlServiceConfig.class);
    requireBinding(GraphQlRequestContextBuilder.class);
    requireBinding(GrpcChannelRegistry.class);
  }
}
