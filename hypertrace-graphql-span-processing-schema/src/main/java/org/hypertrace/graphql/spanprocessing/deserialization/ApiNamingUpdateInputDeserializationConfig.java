package org.hypertrace.graphql.spanprocessing.deserialization;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ApiNamingRuleUpdate;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingLogicalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRelationalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

public class ApiNamingUpdateInputDeserializationConfig implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return ApiNamingRuleUpdate.ARGUMENT_NAME;
  }

  @Override
  public Class<ApiNamingRuleUpdate> getArgumentSchema() {
    return ApiNamingRuleUpdate.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(ApiNamingRuleUpdate.class, DefaultApiNamingRuleUpdate.class)
            .addAbstractTypeMapping(
                SpanProcessingRuleFilter.class, DefaultSpanProcessingRuleFilter.class)
            .addAbstractTypeMapping(
                SpanProcessingRelationalFilter.class, DefaultSpanProcessingRelationalFilter.class)
            .addAbstractTypeMapping(
                SpanProcessingLogicalFilter.class, DefaultSpanProcessingLogicalFilter.class));
  }

  @Value
  @Accessors(fluent = true)
  @Jacksonized
  @Builder
  private static class DefaultApiNamingRuleUpdate implements ApiNamingRuleUpdate {
    String id;
    String name;
    SpanProcessingRuleFilter spanFilter;
    boolean disabled;
    String regex;
    String value;
  }
}
