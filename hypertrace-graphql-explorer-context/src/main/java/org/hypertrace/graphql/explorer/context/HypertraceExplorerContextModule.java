package org.hypertrace.graphql.explorer.context;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.graphql.explorer.schema.argument.ExplorerContext;

public class HypertraceExplorerContextModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Key.get(new TypeLiteral<Class<? extends ExplorerContext>>() {}))
        .toInstance(HypertraceExplorerContext.class);
    bind(Key.get(new TypeLiteral<Converter<ExplorerContext, String>>() {}))
        .to(HypertraceExploreContextStringConverter.class);
    bind(Key.get(new TypeLiteral<Converter<ExplorerContext, AttributeModelScope>>() {}))
        .to(HypertraceExplorerContextScopeConverter.class);
  }
}
