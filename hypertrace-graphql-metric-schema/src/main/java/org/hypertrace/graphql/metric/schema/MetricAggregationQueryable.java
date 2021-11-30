package org.hypertrace.graphql.metric.schema;

import static java.util.Objects.requireNonNull;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.Optional;
import javax.annotation.Nullable;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.graphql.metric.schema.argument.MetricKeyArgument;

public interface MetricAggregationQueryable {
  String METRIC_FIELD_NAME = "metric";

  @GraphQLField
  @GraphQLName(METRIC_FIELD_NAME)
  @GraphQLNonNull
  default BaselinedMetricAggregationContainer metric(
      @GraphQLName(MetricKeyArgument.ARGUMENT_NAME) @Nullable String key,
      @GraphQLName(AttributeExpression.ARGUMENT_NAME) @Nullable AttributeExpression expression) {
    return metric(
        Optional.ofNullable(expression)
            .orElseGet(() -> AttributeExpression.forAttributeKey(requireNonNull(key))));
  }

  BaselinedMetricAggregationContainer metric(AttributeExpression attributeExpression);
}
