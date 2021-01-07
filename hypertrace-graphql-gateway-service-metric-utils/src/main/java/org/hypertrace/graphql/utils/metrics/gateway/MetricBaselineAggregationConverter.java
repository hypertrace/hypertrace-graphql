package org.hypertrace.graphql.utils.metrics.gateway;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.baseline.Baseline;
import org.hypertrace.graphql.metric.schema.MetricBaselineAggregation;

class MetricBaselineAggregationConverter implements Converter<Baseline, MetricBaselineAggregation> {

  @Override
  public Single<MetricBaselineAggregation> convert(Baseline baseline) {
    return Single.just(getBaselinedMetricAggregation(baseline));
  }

  private MetricBaselineAggregation getBaselinedMetricAggregation(Baseline baseline) {
    if (baseline == null) {
      new MetricBaselineAggregationDefaultInstance();
    }
    return new MetricBaselineAggregationImpl(baseline);
  }

  static class MetricBaselineAggregationImpl implements MetricBaselineAggregation {
    private final Baseline baseline;

    public MetricBaselineAggregationImpl(Baseline baseline) {
      this.baseline = baseline;
    }

    @Override
    public Double value() {
      return baseline.getValue().getDouble();
    }

    @Override
    public Double lowerBound() {
      return baseline.getValue().getDouble();
    }

    @Override
    public Double upperBound() {
      return baseline.getValue().getDouble();
    }
  }

  static class MetricBaselineAggregationDefaultInstance implements MetricBaselineAggregation {

    @Override
    public Double lowerBound() {
      return null;
    }

    @Override
    public Double upperBound() {
      return null;
    }

    @Override
    public Double value() {
      return null;
    }
  }
}
