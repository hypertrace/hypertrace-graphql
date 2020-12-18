package org.hypertrace.graphql.metric.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

import java.time.Instant;

public interface BaselineMetricInterval extends BaselineMetricAggregationContainer {
    String BASELINE_AGGREGATION_VALUE = "baseline";

    @GraphQLField
    @GraphQLNonNull
    Instant startTime();

    @GraphQLField
    @GraphQLNonNull
    Instant endTime();

    @GraphQLName(BASELINE_AGGREGATION_VALUE)
    @GraphQLField
    MetricBaselineAggregation baseline();


}
