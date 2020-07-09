package org.hypertrace.core.graphql.trace.deserialization;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.core.graphql.trace.schema.arguments.TraceTypeArgument;

public class TraceDeserializationModule extends AbstractModule {
  @Override
  protected void configure() {

    Multibinder<ArgumentDeserializationConfig> deserializationConfigMultibinder =
        Multibinder.newSetBinder(binder(), ArgumentDeserializationConfig.class);

    deserializationConfigMultibinder
        .addBinding()
        .toInstance(
            ArgumentDeserializationConfig.forPrimitive(
                TraceTypeArgument.ARGUMENT_NAME, TraceTypeArgument.class));
  }
}
