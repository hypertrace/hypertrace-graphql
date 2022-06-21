package org.hypertrace.graphql.spanprocessing.deserialization;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.spanprocessing.schema.mutation.IncludeSpanRuleCreate;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingLogicalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRelationalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

public class IncludeSpanCreateInputDeserializationConfig implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return IncludeSpanRuleCreate.ARGUMENT_NAME;
  }

  @Override
  public Class<IncludeSpanRuleCreate> getArgumentSchema() {
    return IncludeSpanRuleCreate.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(IncludeSpanRuleCreate.class, DefaultIncludeSpanRuleCreate.class)
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
  private static class DefaultIncludeSpanRuleCreate implements IncludeSpanRuleCreate {
    String name;
    SpanProcessingRuleFilter spanFilter;
    boolean disabled;
  }
}
