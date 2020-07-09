package org.hypertrace.core.graphql.attributes;

import static org.hypertrace.core.graphql.attributes.AttributeModelScope.SPAN;
import static org.hypertrace.core.graphql.attributes.AttributeModelScope.TRACE;
import static org.hypertrace.core.graphql.attributes.IdMapping.forForeignId;
import static org.hypertrace.core.graphql.attributes.IdMapping.forId;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
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

    Multibinder<IdMapping> idBinder = Multibinder.newSetBinder(binder(), IdMapping.class);
    idBinder.addBinding().toInstance(forId(SPAN, "id"));
    idBinder.addBinding().toInstance(forForeignId(SPAN, TRACE, "traceId"));

    idBinder.addBinding().toInstance(forId(TRACE, "id"));
  }
}
