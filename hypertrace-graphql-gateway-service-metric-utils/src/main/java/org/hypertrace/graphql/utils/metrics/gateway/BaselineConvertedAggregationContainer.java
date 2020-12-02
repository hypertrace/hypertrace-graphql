package org.hypertrace.graphql.utils.metrics.gateway;

import graphql.annotations.annotationTypes.GraphQLNonNull;
import lombok.AllArgsConstructor;
import org.hypertrace.core.graphql.common.schema.time.TimeUnit;
import org.hypertrace.graphql.metric.schema.BaselineMetricAggregation;
import org.hypertrace.graphql.metric.schema.BaselineMetricAggregationContainer;

import java.time.Duration;
import java.util.Map;

import static org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType.*;
import static org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType.DISTINCT_COUNT;

@AllArgsConstructor
public class BaselineConvertedAggregationContainer implements BaselineMetricAggregationContainer {
        Map<MetricLookupMapKey, BaselineMetricAggregation> metricAggregationMap;

        @Override
        public BaselineMetricAggregation sum() {
            return this.metricAggregationMap.get(MetricLookupMapKey.basicAggregation(SUM));
        }

        @Override
        public BaselineMetricAggregation min() {
            return this.metricAggregationMap.get(MetricLookupMapKey.basicAggregation(MIN));
        }

        @Override
        public BaselineMetricAggregation max() {
            return this.metricAggregationMap.get(MetricLookupMapKey.basicAggregation(MAX));
        }

        @Override
        public BaselineMetricAggregation avg() {
            return this.metricAggregationMap.get(MetricLookupMapKey.basicAggregation(AVG));
        }

        @Override
        public BaselineMetricAggregation avgrate(int size, TimeUnit units) {
            return this.metricAggregationMap.get(
                    MetricLookupMapKey.avgRateAggregation(Duration.of(size, units.getChronoUnit())));
        }

        @Override
        public BaselineMetricAggregation percentile(int size) {
            return this.metricAggregationMap.get(MetricLookupMapKey.percentileAggregation(size));
        }

        @Override
        public @GraphQLNonNull BaselineMetricAggregation count() {
            return this.metricAggregationMap.get(MetricLookupMapKey.basicAggregation(COUNT));
        }

        @Override
        public @GraphQLNonNull BaselineMetricAggregation distinctcount() {
            return this.metricAggregationMap.get(MetricLookupMapKey.basicAggregation(DISTINCT_COUNT));
        }
    }
