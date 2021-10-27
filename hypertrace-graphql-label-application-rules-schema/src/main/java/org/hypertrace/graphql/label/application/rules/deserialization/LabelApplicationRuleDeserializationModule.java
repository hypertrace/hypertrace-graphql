package org.hypertrace.graphql.label.application.rules.deserialization;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;

public class LabelApplicationRuleDeserializationModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<ArgumentDeserializationConfig> deserializationConfigBinder =
        Multibinder.newSetBinder(binder(), ArgumentDeserializationConfig.class);

    deserializationConfigBinder
        .addBinding()
        .to(CreateLabelApplicationRuleDeserializationConfig.class);
    deserializationConfigBinder.addBinding().to(LabelApplicationRuleDeserializationConfig.class);
  }
}
