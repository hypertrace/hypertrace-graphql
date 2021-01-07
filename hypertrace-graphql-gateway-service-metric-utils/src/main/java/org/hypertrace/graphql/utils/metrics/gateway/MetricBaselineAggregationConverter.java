package org.hypertrace.graphql.utils.metrics.gateway;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.baseline.Baseline;
import org.hypertrace.graphql.metric.schema.MetricBaselineAggregation;

class MetricBaselineAggregationConverter
    implements Converter<Baseline, MetricBaselineAggregation> {

  @Override
  public Single<MetricBaselineAggregation> convert(Baseline baseline) {
    return Single.just(getBaselinedMetricAggregation(baseline));
  }

  private MetricBaselineAggregation getBaselinedMetricAggregation(Baseline baseline) {
    if (baseline == null) {
      // Return default instance
    }
    return new MetricBaselineAggregation() {
      @Override
      public Double lowerBound() {
        return baseline.getLowerBound().getDouble();
      }

      @Override
      public Double upperBound() {
        return baseline.getUpperBound().getDouble();
      }

      @Override
      public Double value() {
        return baseline.getValue().getDouble();
      }
    };
  }
}
