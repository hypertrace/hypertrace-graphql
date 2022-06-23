package org.hypertrace.graphql.spanprocessing.schema.samplingconfigs;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;

@GraphQLName(SamplingConfigInfo.TYPE_NAME)
public interface SamplingConfigInfo {
  String TYPE_NAME = "SamplingConfigInfo";

  String SPAN_FILTER_KEY = "spanFilter";
  String RATE_LIMIT_CONFIG_KEY = "rateLimitConfig";

  @GraphQLField
  @GraphQLName(RATE_LIMIT_CONFIG_KEY)
  @GraphQLNonNull
  RateLimitConfig rateLimitConfig();

  @GraphQLField
  @GraphQLName(SPAN_FILTER_KEY)
  @GraphQLNonNull
  SpanProcessingRuleFilter spanFilter();
}
