package org.hypertrace.graphql.metric.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(BaselinedMetricAggregation.BASELINE_METRIC_AGGREGATION_TYPE_NAME)
public interface BaselinedMetricAggregation extends MetricAggregation {

    String BASELINE_METRIC_AGGREGATION_TYPE_NAME = "BaselineMetricAggregation";
    String BASELINE_AGGREGATION_VALUE = "baseline";

    @GraphQLName(BASELINE_AGGREGATION_VALUE)
    @GraphQLField
    MetricBaselineAggregation baseline();
}
