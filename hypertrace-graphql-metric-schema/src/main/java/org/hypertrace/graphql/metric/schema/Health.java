package org.hypertrace.graphql.metric.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

public interface Health {
    String HEALTH_VALUE = "value";
    String HEALTH_MESSAGE = "message";

    @GraphQLName(HEALTH_VALUE)
    @GraphQLField
    Double value();

    @GraphQLName(HEALTH_MESSAGE)
    @GraphQLField
    String message();
}
