package org.hypertrace.graphql.entity.attributes;

import static org.hypertrace.core.graphql.attributes.AttributeModelScope.API;
import static org.hypertrace.core.graphql.attributes.AttributeModelScope.API_TRACE;
import static org.hypertrace.core.graphql.attributes.AttributeModelScope.BACKEND;
import static org.hypertrace.core.graphql.attributes.AttributeModelScope.BACKEND_TRACE;
import static org.hypertrace.core.graphql.attributes.AttributeModelScope.SERVICE;
import static org.hypertrace.core.graphql.attributes.AttributeModelScope.SPAN;
import static org.hypertrace.core.graphql.attributes.AttributeModelScope.TRACE;
import static org.hypertrace.core.graphql.attributes.IdMapping.forForeignId;
import static org.hypertrace.core.graphql.attributes.IdMapping.forId;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.attributes.IdMapping;

public class HypertraceEntityAttributesModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<IdMapping> idBinder = Multibinder.newSetBinder(binder(), IdMapping.class);

    idBinder.addBinding().toInstance(forId(API, "id"));

    idBinder.addBinding().toInstance(forForeignId(API, SERVICE, "serviceId"));

    idBinder.addBinding().toInstance(forId(SERVICE, "id"));

    idBinder.addBinding().toInstance(forId(BACKEND, "id"));

    // TODO find new home for api, backend traces
    idBinder.addBinding().toInstance(forId(API_TRACE, "apiTraceId"));
    idBinder.addBinding().toInstance(forForeignId(API_TRACE, SERVICE, "serviceId"));
    idBinder.addBinding().toInstance(forForeignId(API_TRACE, API, "apiId"));
    idBinder.addBinding().toInstance(forForeignId(API_TRACE, TRACE, "traceId"));

    idBinder.addBinding().toInstance(forId(BACKEND_TRACE, "backendTraceId"));
    idBinder.addBinding().toInstance(forForeignId(BACKEND_TRACE, SERVICE, "callerServiceId"));
    idBinder.addBinding().toInstance(forForeignId(BACKEND_TRACE, API, "callerApiId"));
    idBinder.addBinding().toInstance(forForeignId(BACKEND_TRACE, TRACE, "traceId"));
    idBinder.addBinding().toInstance(forForeignId(BACKEND_TRACE, BACKEND, "backendId"));

    idBinder.addBinding().toInstance(forForeignId(SPAN, API_TRACE, "apiTraceId"));
  }
}
