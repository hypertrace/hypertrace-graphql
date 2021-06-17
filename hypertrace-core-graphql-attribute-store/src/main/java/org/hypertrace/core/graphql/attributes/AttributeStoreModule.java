package org.hypertrace.core.graphql.attributes;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.multibindings.Multibinder;
import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Scheduler;
import org.hypertrace.core.graphql.rx.BoundedIoScheduler;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;

public class AttributeStoreModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(AttributeStore.class).to(CachingAttributeStore.class);
    Multibinder.newSetBinder(binder(), IdMapping.class);
    Multibinder.newSetBinder(binder(), IdMappingLoader.class);
    requireBinding(GraphQlServiceConfig.class);
    requireBinding(GrpcContextBuilder.class);
    requireBinding(CallCredentials.class);
    requireBinding(GrpcChannelRegistry.class);
    requireBinding(Key.get(Scheduler.class, BoundedIoScheduler.class));
  }
}
