package org.hypertrace.core.graphql.atttributes.scopes;

import static org.hypertrace.core.graphql.attributes.IdMapping.forForeignId;
import static org.hypertrace.core.graphql.attributes.IdMapping.forId;
import static org.hypertrace.core.graphql.atttributes.scopes.HypertraceCoreAttributeScopeString.LOG_EVENT;
import static org.hypertrace.core.graphql.atttributes.scopes.HypertraceCoreAttributeScopeString.SPAN;
import static org.hypertrace.core.graphql.atttributes.scopes.HypertraceCoreAttributeScopeString.TRACE;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.attributes.IdMapping;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;

public class HypertraceCoreAttributeScopeModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Key.get(new TypeLiteral<Class<? extends AttributeScope>>() {}))
        .toInstance(HypertraceCoreAttributeScope.class);
    Multibinder<IdMapping> idBinder = Multibinder.newSetBinder(binder(), IdMapping.class);
    idBinder.addBinding().toInstance(forId(SPAN, "id"));
    idBinder.addBinding().toInstance(forForeignId(SPAN, TRACE, "traceId"));
    idBinder.addBinding().toInstance(forForeignId(LOG_EVENT, SPAN, "spanId"));
    idBinder.addBinding().toInstance(forId(TRACE, "id"));
  }
}
