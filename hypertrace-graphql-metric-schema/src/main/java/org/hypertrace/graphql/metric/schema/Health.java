package org.hypertrace.graphql.metric.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

public interface Health {
    String HEALTH_VALUE = "value";
    String HEALTH_MESSAGE = "message";

    @GraphQLName(HEALTH_VALUE)
    @GraphQLNonNull
    @GraphQLField
    Double value();

    @GraphQLName(HEALTH_MESSAGE)
    @GraphQLNonNull
    @GraphQLField
    String message();
}
