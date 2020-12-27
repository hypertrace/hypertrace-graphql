package org.hypertrace.core.graphql.common.deserialization;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeKeyArgument;
import org.hypertrace.core.graphql.common.schema.id.IdArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.LimitArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.OffsetArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.space.SpaceArgument;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;

public class CommonDeserializationModule extends AbstractModule {

  @Override
  protected void configure() {
    Multibinder<ArgumentDeserializationConfig> deserializationConfigMultibinder =
        Multibinder.newSetBinder(binder(), ArgumentDeserializationConfig.class);

    deserializationConfigMultibinder.addBinding().to(TimeRangeArgumentDeserializationConfig.class);
    deserializationConfigMultibinder
        .addBinding()
        .toInstance(
            ArgumentDeserializationConfig.forPrimitive(IdArgument.ARGUMENT_NAME, IdArgument.class));
    deserializationConfigMultibinder
        .addBinding()
        .toInstance(
            ArgumentDeserializationConfig.forPrimitive(
                LimitArgument.ARGUMENT_NAME, LimitArgument.class));
    deserializationConfigMultibinder
        .addBinding()
        .toInstance(
            ArgumentDeserializationConfig.forPrimitive(
                OffsetArgument.ARGUMENT_NAME, OffsetArgument.class));
    deserializationConfigMultibinder.addBinding().to(OrderArgumentDeserializationConfig.class);
    deserializationConfigMultibinder.addBinding().to(FilterArgumentDeserializationConfig.class);

    deserializationConfigMultibinder
        .addBinding()
        .toInstance(
            ArgumentDeserializationConfig.forPrimitive(
                AttributeKeyArgument.ARGUMENT_NAME, AttributeKeyArgument.class));

    deserializationConfigMultibinder
        .addBinding()
        .toInstance(
            ArgumentDeserializationConfig.forPrimitive(
                SpaceArgument.ARGUMENT_NAME, SpaceArgument.class));

    requireBinding(Key.get(new TypeLiteral<Class<? extends AttributeScope>>() {}));
  }
}
