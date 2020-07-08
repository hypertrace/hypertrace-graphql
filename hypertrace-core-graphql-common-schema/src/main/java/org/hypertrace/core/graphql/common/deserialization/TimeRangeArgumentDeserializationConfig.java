package org.hypertrace.core.graphql.common.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.time.Instant;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;

class TimeRangeArgumentDeserializationConfig implements ArgumentDeserializationConfig {

  @Override
  public String getArgumentKey() {
    return TimeRangeArgument.ARGUMENT_NAME;
  }

  @Override
  public Class<TimeRangeArgument> getArgumentSchema() {
    return TimeRangeArgument.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule().addAbstractTypeMapping(TimeRangeArgument.class, DefaultTimeRange.class));
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class DefaultTimeRange implements TimeRangeArgument {
    @JsonProperty(TIME_RANGE_ARGUMENT_START_TIME)
    Instant startTime;

    @JsonProperty(TIME_RANGE_ARGUMENT_END_TIME)
    Instant endTime;
  }
}
