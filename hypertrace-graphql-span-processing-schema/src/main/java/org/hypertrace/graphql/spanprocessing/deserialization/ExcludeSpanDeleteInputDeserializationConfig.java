package org.hypertrace.graphql.spanprocessing.deserialization;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ExcludeSpanRuleDelete;

public class ExcludeSpanDeleteInputDeserializationConfig implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return ExcludeSpanRuleDelete.ARGUMENT_NAME;
  }

  @Override
  public Class<ExcludeSpanRuleDelete> getArgumentSchema() {
    return ExcludeSpanRuleDelete.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(
                ExcludeSpanRuleDelete.class, DefaultExcludeSpanRuleDelete.class));
  }

  @Value
  @Accessors(fluent = true)
  @Jacksonized
  @Builder
  private static class DefaultExcludeSpanRuleDelete implements ExcludeSpanRuleDelete {
    String id;
  }
}
