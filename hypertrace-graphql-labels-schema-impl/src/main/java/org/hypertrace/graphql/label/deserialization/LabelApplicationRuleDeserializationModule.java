package org.hypertrace.graphql.label.deserialization;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;

public class LabelApplicationRuleDeserializationModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<ArgumentDeserializationConfig> deserializationConfigBinder =
        Multibinder.newSetBinder(binder(), ArgumentDeserializationConfig.class);

    deserializationConfigBinder.addBinding().to(LabelApplicationRuleDeserializationConfig.class);

    deserializationConfigBinder
        .addBinding()
        .toInstance(
            ArgumentDeserializationConfig.forPrimitive(
                LabelApplicationRuleIdArgument.ARGUMENT_NAME,
                LabelApplicationRuleIdArgument.class));
  }
}
