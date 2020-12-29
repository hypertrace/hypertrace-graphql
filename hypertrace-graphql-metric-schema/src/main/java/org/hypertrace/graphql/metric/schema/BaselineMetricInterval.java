package org.hypertrace.graphql.metric.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

import java.time.Instant;

@GraphQLName(BaselineMetricInterval.BASELINE_METRIC_INTERVAL)
public interface BaselineMetricInterval extends BaselineMetricAggregationContainer {
    String BASELINE_METRIC_INTERVAL = "baselineInterval";

    @GraphQLField
    @GraphQLNonNull
    Instant startTime();

    @GraphQLField
    @GraphQLNonNull
    Instant endTime();

}
