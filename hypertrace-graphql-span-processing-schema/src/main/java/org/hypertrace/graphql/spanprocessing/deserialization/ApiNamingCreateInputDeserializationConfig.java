package org.hypertrace.graphql.spanprocessing.deserialization;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ApiNamingRuleCreate;
import org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRuleConfig;
import org.hypertrace.graphql.spanprocessing.schema.rule.SegmentMatchingBasedRuleConfig;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingLogicalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRelationalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

public class ApiNamingCreateInputDeserializationConfig implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return ApiNamingRuleCreate.ARGUMENT_NAME;
  }

  @Override
  public Class<ApiNamingRuleCreate> getArgumentSchema() {
    return ApiNamingRuleCreate.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(ApiNamingRuleCreate.class, DefaultApiNamingRuleCreate.class)
            .addAbstractTypeMapping(ApiNamingRuleConfig.class, DefaultApiNamingRuleConfig.class)
            .addAbstractTypeMapping(
                SegmentMatchingBasedRuleConfig.class, DefaultSegmentMatchingBasedRuleConfig.class)
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
  private static class DefaultApiNamingRuleCreate implements ApiNamingRuleCreate {
    String name;
    SpanProcessingRuleFilter spanFilter;
    ApiNamingRuleConfig apiNamingRuleConfig;
    boolean disabled;
  }
}
