package org.hypertrace.core.graphql.common.deserialization;

import static org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument.ARGUMENT_NAME;
import static org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument.TIME_RANGE_ARGUMENT_END_TIME;
import static org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument.TIME_RANGE_ARGUMENT_START_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.multibindings.Multibinder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.deserialization.GraphQlDeserializationRegistryModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimeRangeArgumentDeserializationConfigTest {
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
                        .to(TimeRangeArgumentDeserializationConfig.class);
                  }
                })
            .getInstance(ArgumentDeserializer.class);
  }

  @Test
  void deserializesValueIfPresent() {
    Instant endTime = Instant.now();
    Instant startTime = endTime.minus(1, ChronoUnit.HOURS);
    Map<String, Object> argMap =
        Map.of(
            ARGUMENT_NAME,
            Map.of(
                TIME_RANGE_ARGUMENT_START_TIME, startTime, TIME_RANGE_ARGUMENT_END_TIME, endTime));

    TimeRangeArgument result =
        this.argumentDeserializer.deserializeObject(argMap, TimeRangeArgument.class).orElseThrow();
    assertEquals(startTime, result.startTime());
    assertEquals(endTime, result.endTime());
  }
}
