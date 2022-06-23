package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.RateLimitConfig;

@GraphQLName(SamplingConfigUpdate.TYPE_NAME)
public interface SamplingConfigUpdate extends Identifiable {
  String TYPE_NAME = "SamplingConfigUpdate";
  String ARGUMENT_NAME = "input";

  String RATE_LIMIT_CONFIG_KEY = "rateLimitConfig";
  String SPAN_PROCESSING_FILTER_KEY = "spanFilter";

  @GraphQLField
  @GraphQLName(RATE_LIMIT_CONFIG_KEY)
  @GraphQLNonNull
  RateLimitConfig rateLimitConfig();

  @GraphQLField
  @GraphQLName(SPAN_PROCESSING_FILTER_KEY)
  @GraphQLNonNull
  SpanProcessingRuleFilter spanFilter();
}
