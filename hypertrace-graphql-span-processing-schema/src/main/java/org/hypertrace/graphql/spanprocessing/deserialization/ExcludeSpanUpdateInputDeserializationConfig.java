package org.hypertrace.graphql.spanprocessing.deserialization;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ExcludeSpanRuleUpdate;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingFilterField;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingLogicalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingLogicalOperator;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRelationalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRelationalOperator;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

public class ExcludeSpanUpdateInputDeserializationConfig implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return ExcludeSpanRuleUpdate.ARGUMENT_NAME;
  }

  @Override
  public Class<ExcludeSpanRuleUpdate> getArgumentSchema() {
    return ExcludeSpanRuleUpdate.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(ExcludeSpanRuleUpdate.class, DefaultExcludeSpanRuleUpdate.class)
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
  private static class DefaultExcludeSpanRuleUpdate implements ExcludeSpanRuleUpdate {
    String id;
    String name;
    SpanProcessingRuleFilter spanFilter;
    boolean disabled;
  }

  @Value
  @Accessors(fluent = true)
  @Jacksonized
  @Builder
  private static class DefaultSpanProcessingRuleFilter implements SpanProcessingRuleFilter {
    SpanProcessingLogicalFilter logicalSpanFilter;
    SpanProcessingRelationalFilter relationalSpanFilter;
  }

  @Value
  @Accessors(fluent = true)
  @Jacksonized
  @Builder
  private static class DefaultSpanProcessingLogicalFilter implements SpanProcessingLogicalFilter {
    SpanProcessingLogicalOperator logicalOperator;
    List<SpanProcessingRuleFilter> spanFilters;
  }

  @Value
  @Accessors(fluent = true)
  @Jacksonized
  @Builder
  private static class DefaultSpanProcessingRelationalFilter
      implements SpanProcessingRelationalFilter {
    String key;
    SpanProcessingFilterField field;
    SpanProcessingRelationalOperator relationalOperator;
    Object value;
  }
}
