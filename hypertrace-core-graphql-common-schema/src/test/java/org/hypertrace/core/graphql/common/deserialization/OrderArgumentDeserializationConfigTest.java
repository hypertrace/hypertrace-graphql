package org.hypertrace.core.graphql.common.deserialization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.multibindings.Multibinder;
import java.util.List;
import java.util.Map;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderDirection;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.deserialization.GraphQlDeserializationRegistryModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderArgumentDeserializationConfigTest {
  private ArgumentDeserializer argumentDeserializer;

  @BeforeEach
  void beforeEach() {
    this.argumentDeserializer =
        Guice.createInjector(
                new GraphQlDeserializationRegistryModule(),
                new AbstractModule() {
                  @Override
                  protected void configure() {
                    Multibinder.newSetBinder(binder(), ArgumentDeserializationConfig.class)
                        .addBinding()
                        .to(OrderArgumentDeserializationConfig.class);
                  }
                })
            .getInstance(ArgumentDeserializer.class);
  }

  @Test
  void deserializesValueIfPresent() {
    Map<String, Object> argMap =
        Map.of(
            OrderArgument.ARGUMENT_NAME,
            List.of(
                Map.of(
                    OrderArgument.ORDER_KEY_NAME,
                    "fooKey",
                    OrderArgument.ORDER_DIRECTION_NAME,
                    OrderDirection.ASC),
                Map.of(OrderArgument.ORDER_KEY_NAME, "barKey")));

    List<OrderArgument> result =
        this.argumentDeserializer.deserializeObjectList(argMap, OrderArgument.class).orElseThrow();
    assertEquals(2, result.size());
    assertEquals("fooKey", result.get(0).key());
    Assertions.assertEquals(OrderDirection.ASC, result.get(0).direction());
    assertEquals("barKey", result.get(1).key());
    Assertions.assertEquals(OrderDirection.DESC, result.get(1).direction());
  }
}
