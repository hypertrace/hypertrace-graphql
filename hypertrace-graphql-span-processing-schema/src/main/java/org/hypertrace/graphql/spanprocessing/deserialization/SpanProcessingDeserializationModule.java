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

    multibinder.addBinding().to(IncludeSpanCreateInputDeserializationConfig.class);
    multibinder.addBinding().to(IncludeSpanUpdateInputDeserializationConfig.class);
    multibinder.addBinding().to(IncludeSpanDeleteInputDeserializationConfig.class);

    multibinder.addBinding().to(ApiNamingCreateInputDeserializationConfig.class);
    multibinder.addBinding().to(ApiNamingUpdateInputDeserializationConfig.class);
    multibinder.addBinding().to(ApiNamingDeleteInputDeserializationConfig.class);
  }
}
