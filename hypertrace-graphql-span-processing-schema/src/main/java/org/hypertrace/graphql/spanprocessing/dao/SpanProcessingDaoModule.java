package org.hypertrace.graphql.spanprocessing.dao;

import com.google.inject.AbstractModule;
import io.grpc.CallCredentials;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;

public class SpanProcessingDaoModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(SpanProcessingRuleDao.class).to(ConfigServiceSpanProcessingRuleDao.class);

    requireBinding(HypertraceGraphQlServiceConfig.class);
    requireBinding(CallCredentials.class);
    requireBinding(GrpcContextBuilder.class);
    requireBinding(GrpcChannelRegistry.class);
  }
}
