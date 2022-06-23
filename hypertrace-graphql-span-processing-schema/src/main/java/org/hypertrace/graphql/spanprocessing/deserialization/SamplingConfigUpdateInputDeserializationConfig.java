package org.hypertrace.graphql.spanprocessing.deserialization;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.spanprocessing.schema.mutation.SamplingConfigUpdate;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingLogicalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRelationalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.RateLimit;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.RateLimitConfig;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.WindowedRateLimit;

public class SamplingConfigUpdateInputDeserializationConfig
    implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return SamplingConfigUpdate.ARGUMENT_NAME;
  }

  @Override
  public Class<SamplingConfigUpdate> getArgumentSchema() {
    return SamplingConfigUpdate.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(SamplingConfigUpdate.class, DefaultSamplingConfigUpdate.class)
            .addAbstractTypeMapping(RateLimitConfig.class, DefaultRateLimitConfig.class)
            .addAbstractTypeMapping(RateLimit.class, DefaultRateLimit.class)
            .addAbstractTypeMapping(WindowedRateLimit.class, DefaultWindowedRateLimit.class)
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
  private static class DefaultSamplingConfigUpdate implements SamplingConfigUpdate {
    String id;
    RateLimitConfig rateLimitConfig;
    SpanProcessingRuleFilter spanFilter;
  }
}
