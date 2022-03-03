package org.hypertrace.graphql.spanprocessing.deserialization;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ApiNamingRuleDelete;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ExcludeSpanRuleDelete;

public class ApiNamingDeleteInputDeserializationConfig implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return ExcludeSpanRuleDelete.ARGUMENT_NAME;
  }

  @Override
  public Class<ApiNamingRuleDelete> getArgumentSchema() {
    return ApiNamingRuleDelete.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(ApiNamingRuleDelete.class, DefaultApiNamingRuleDelete.class));
  }

  @Value
  @Accessors(fluent = true)
  @Jacksonized
  @Builder
  private static class DefaultApiNamingRuleDelete implements ApiNamingRuleDelete {
    String id;
  }
}
