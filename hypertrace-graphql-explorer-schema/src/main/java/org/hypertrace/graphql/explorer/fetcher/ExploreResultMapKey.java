package org.hypertrace.graphql.explorer.fetcher;

import static org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType.AVGRATE;
import static org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType.PERCENTILE;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.request.MetricArguments;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExploreResultMapKey {
  @Nonnull AttributeExpression attributeExpression;
  @Nullable MetricAggregationType aggregationType;
  @Nullable List<Object> arguments;

  public static ExploreResultMapKey attribute(AttributeExpression attributeExpression) {
    return new ExploreResultMapKey(attributeExpression, null, null);
  }

  public static ExploreResultMapKey forAggregationRequest(
      MetricAggregationRequest aggregationRequest, MetricAggregationType aggregationType) {
    return new ExploreResultMapKey(
        aggregationRequest.attributeExpression().value(),
        aggregationType,
        aggregationRequest.arguments());
  }

  public static ExploreResultMapKey basicAggregation(
      AttributeExpression attributeExpression, MetricAggregationType aggregationType) {
    return new ExploreResultMapKey(attributeExpression, aggregationType, Collections.emptyList());
  }

  public static ExploreResultMapKey avgRateAggregation(
      AttributeExpression attributeExpression, Duration duration) {
    return new ExploreResultMapKey(
        attributeExpression, AVGRATE, MetricArguments.avgRateWithPeriod(duration));
  }

  public static ExploreResultMapKey percentileAggregation(
      AttributeExpression attributeExpression, int value) {
    return new ExploreResultMapKey(
        attributeExpression, PERCENTILE, MetricArguments.percentileWithSize(value));
  }

  public static ExploreResultMapKey intervalStart() {
    return new ExploreResultMapKey(
        AttributeExpression.forAttributeKey("__intervalStart__"), null, null);
  }
}
