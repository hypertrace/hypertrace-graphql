package org.hypertrace.graphql.label.deserialization;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;

public class LabelDeserializationModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<ArgumentDeserializationConfig> deserializationConfigBinder =
        Multibinder.newSetBinder(binder(), ArgumentDeserializationConfig.class);

    deserializationConfigBinder.addBinding().to(CreateLabelDeserializationConfig.class);
    deserializationConfigBinder
        .addBinding()
        .toInstance(
            ArgumentDeserializationConfig.forPrimitive(
                LabelIdArgument.ARGUMENT_NAME, LabelIdArgument.class));
    deserializationConfigBinder.addBinding().to(LabelDeserializationConfig.class);
  }
}
