package org.hypertrace.core.graphql.atttributes.scopes;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.utils.Converter;

public class HypertraceCoreAttributeScopeModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Key.get(new TypeLiteral<Class<? extends AttributeScope>>() {}))
        .toInstance(HypertraceCoreAttributeScope.class);
    bind(Key.get(new TypeLiteral<Converter<AttributeModelScope, AttributeScope>>() {}))
        .to(HypertraceCoreAttributeScopeConverter.class);
    bind(Key.get(new TypeLiteral<Converter<AttributeScope, AttributeModelScope>>() {}))
        .to(HypertraceCoreAttributeModelScopeConverter.class);
  }
}
