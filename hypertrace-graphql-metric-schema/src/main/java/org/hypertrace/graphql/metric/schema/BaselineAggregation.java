package org.hypertrace.graphql.metric.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

public interface BaselineAggregation extends MetricAggregation {

    String BASELINE_AGGREGATION_LOWER_VALUE = "lowerValue";
    String BASELINE_AGGREGATION_UPPER_VALUE = "upperValue";
    String HEALTH_VALUE = "health";

    @GraphQLName(BASELINE_AGGREGATION_LOWER_VALUE)
    @GraphQLField
    Double lowerValue();

    @GraphQLName(BASELINE_AGGREGATION_UPPER_VALUE)
    @GraphQLField
    Double upperValue();

    @GraphQLName(HEALTH_VALUE)
    @GraphQLField
    Health health();

}
