package org.hypertrace.graphql.spanprocessing.deserialization;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;

public class SpanProcessingDeserializationModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<ArgumentDeserializationConfig> multibinder =
        Multibinder.newSetBinder(binder(), ArgumentDeserializationConfig.class);

    multibinder.addBinding().to(ExcludeSpanCreateInputDeserializationConfig.class);
    multibinder.addBinding().to(ExcludeSpanUpdateInputDeserializationConfig.class);
    multibinder.addBinding().to(ExcludeSpanDeleteInputDeserializationConfig.class);
  }
}
