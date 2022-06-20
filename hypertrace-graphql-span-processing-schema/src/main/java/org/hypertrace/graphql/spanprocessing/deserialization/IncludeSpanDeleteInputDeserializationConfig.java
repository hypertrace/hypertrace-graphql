package org.hypertrace.graphql.spanprocessing.deserialization;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.spanprocessing.schema.mutation.IncludeSpanRuleDelete;

public class IncludeSpanDeleteInputDeserializationConfig implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return IncludeSpanRuleDelete.ARGUMENT_NAME;
  }

  @Override
  public Class<IncludeSpanRuleDelete> getArgumentSchema() {
    return IncludeSpanRuleDelete.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(
                IncludeSpanRuleDelete.class, DefaultIncludeSpanRuleDelete.class));
  }

  @Value
  @Accessors(fluent = true)
  @Jacksonized
  @Builder
  private static class DefaultIncludeSpanRuleDelete implements IncludeSpanRuleDelete {
    String id;
  }
}
