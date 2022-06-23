package org.hypertrace.graphql.spanprocessing.deserialization;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.spanprocessing.schema.mutation.SamplingConfigDelete;

public class SamplingConfigDeleteInputDeserializationConfig
    implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return SamplingConfigDelete.ARGUMENT_NAME;
  }

  @Override
  public Class<SamplingConfigDelete> getArgumentSchema() {
    return SamplingConfigDelete.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(SamplingConfigDelete.class, DefaultSamplingConfigDelete.class));
  }

  @Value
  @Accessors(fluent = true)
  @Jacksonized
  @Builder
  private static class DefaultSamplingConfigDelete implements SamplingConfigDelete {
    String id;
  }
}
