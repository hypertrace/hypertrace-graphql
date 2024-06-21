package org.hypertrace.core.graphql.deserialization;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class GraphQlDeserializationRegistryModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ArgumentDeserializer.class).to(DefaultArgumentDeserializer.class);
    Multibinder.newSetBinder(binder(), ArgumentDeserializationConfig.class);
  }
}
