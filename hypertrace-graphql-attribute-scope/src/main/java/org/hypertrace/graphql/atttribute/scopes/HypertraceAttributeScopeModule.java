package org.hypertrace.graphql.atttribute.scopes;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.utils.Converter;

public class HypertraceAttributeScopeModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Key.get(new TypeLiteral<Class<? extends AttributeScope>>() {}))
        .toInstance(HypertraceAttributeScope.class);
    bind(Key.get(new TypeLiteral<Converter<AttributeModelScope, AttributeScope>>() {}))
        .to(HypertraceAttributeScopeConverter.class);
    bind(Key.get(new TypeLiteral<Converter<AttributeScope, AttributeModelScope>>() {}))
        .to(HypertraceAttributeModelScopeConverter.class);
  }
}
