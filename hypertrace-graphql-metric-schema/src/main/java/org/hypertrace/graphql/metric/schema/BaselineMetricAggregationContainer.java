package org.hypertrace.graphql.metric.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.core.graphql.common.schema.time.TimeUnit;
import org.hypertrace.graphql.metric.schema.argument.MetricAggregationAvgRateSizeArgument;
import org.hypertrace.graphql.metric.schema.argument.MetricAggregationAvgRateUnitsArgument;
import org.hypertrace.graphql.metric.schema.argument.MetricAggregationPercentileSizeArgument;

import static org.hypertrace.graphql.metric.schema.MetricAggregationContainer.*;

public interface BaselineMetricAggregationContainer {
    @GraphQLField
    @GraphQLNonNull
    @GraphQLName(METRIC_AGGREGATION_CONTAINER_SUM_KEY)
    BaselineMetricAggregation sum();

    @GraphQLField
    @GraphQLNonNull
    @GraphQLName(METRIC_AGGREGATION_CONTAINER_MIN_KEY)
    BaselineMetricAggregation min();

    @GraphQLField
    @GraphQLNonNull
    @GraphQLName(METRIC_AGGREGATION_CONTAINER_MAX_KEY)
    BaselineMetricAggregation max();

    @GraphQLField
    @GraphQLNonNull
    @GraphQLName(METRIC_AGGREGATION_CONTAINER_AVG_KEY)
    BaselineMetricAggregation avg();

    @GraphQLField
    @GraphQLNonNull
    @GraphQLName(METRIC_AGGREGATION_CONTAINER_COUNT_KEY)
    BaselineMetricAggregation count();

    @GraphQLField
    @GraphQLNonNull
    @GraphQLName(METRIC_AGGREGATION_CONTAINER_DISTINCTCOUNT_KEY)
    BaselineMetricAggregation distinctcount();

    @GraphQLField
    @GraphQLNonNull
    @GraphQLName(METRIC_AGGREGATION_CONTAINER_AVGRATE_KEY)
    BaselineMetricAggregation avgrate(
            @GraphQLNonNull @GraphQLName(MetricAggregationAvgRateSizeArgument.ARGUMENT_NAME) int size,
            @GraphQLNonNull @GraphQLName(MetricAggregationAvgRateUnitsArgument.ARGUMENT_NAME)
                    TimeUnit units);

    @GraphQLField
    @GraphQLNonNull
    @GraphQLName(METRIC_AGGREGATION_CONTAINER_PERCENTILE_KEY)
    BaselineMetricAggregation percentile(
            @GraphQLNonNull @GraphQLName(MetricAggregationPercentileSizeArgument.ARGUMENT_NAME) int size);
}
