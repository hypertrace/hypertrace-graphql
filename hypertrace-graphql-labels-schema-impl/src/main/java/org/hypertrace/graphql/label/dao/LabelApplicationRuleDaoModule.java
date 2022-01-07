package org.hypertrace.graphql.label.dao;

import com.google.inject.AbstractModule;
import io.grpc.CallCredentials;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;

public class LabelApplicationRuleDaoModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(LabelApplicationRuleDao.class).to(LabelApplicationRuleConfigServiceDao.class);
    requireBinding(CallCredentials.class);
    requireBinding(GraphQlServiceConfig.class);
    requireBinding(GrpcChannelRegistry.class);
    requireBinding(GrpcContextBuilder.class);
  }
}
