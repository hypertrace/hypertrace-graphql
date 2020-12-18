package org.hypertrace.graphql.utils.metrics.gateway;

import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.graphql.metric.schema.BaselinedMetricAggregation;
import org.hypertrace.graphql.metric.schema.MetricBaselineAggregation;

class MetricAggregationConverter implements Converter<Value, BaselinedMetricAggregation> {

  private final Converter<Value, Object> valueConverter;

  @Inject
  MetricAggregationConverter(Converter<Value, Object> valueConverter) {
    this.valueConverter = valueConverter;
  }

  @Override
  public Single<BaselinedMetricAggregation> convert(Value value) {
    return this.valueConverter
        .convert(value)
        .cast(Number.class)
        .map(Number::doubleValue)
        .map(BaselinedMetricAggregationImpl::new);
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class BaselinedMetricAggregationImpl implements BaselinedMetricAggregation {
    Double value;

    @Override
    public MetricBaselineAggregation baseline() {
      // TODO Implement this
      return null;
    }
  }
}
