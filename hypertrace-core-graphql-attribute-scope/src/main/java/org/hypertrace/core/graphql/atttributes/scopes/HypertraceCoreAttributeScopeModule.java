package org.hypertrace.core.graphql.atttributes.scopes;

import static org.hypertrace.core.graphql.attributes.IdMapping.forForeignId;
import static org.hypertrace.core.graphql.attributes.IdMapping.forId;
import static org.hypertrace.core.graphql.atttributes.scopes.HypertraceCoreAttributeScopeString.SPAN;
import static org.hypertrace.core.graphql.atttributes.scopes.HypertraceCoreAttributeScopeString.TRACE;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import java.util.Optional;
import org.hypertrace.core.graphql.attributes.IdMapping;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.utils.Converter;

public class HypertraceCoreAttributeScopeModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Key.get(new TypeLiteral<Class<? extends AttributeScope>>() {}))
        .toInstance(HypertraceCoreAttributeScope.class);
    bind(Key.get(new TypeLiteral<Converter<String, Optional<AttributeScope>>>() {}))
        .to(HypertraceCoreAttributeScopeConverter.class);

    Multibinder<IdMapping> idBinder = Multibinder.newSetBinder(binder(), IdMapping.class);
    idBinder.addBinding().toInstance(forId(SPAN, "id"));
    idBinder.addBinding().toInstance(forForeignId(SPAN, TRACE, "traceId"));

    idBinder.addBinding().toInstance(forId(TRACE, "id"));
  }
}
