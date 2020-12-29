package org.hypertrace.graphql.utils.metrics.gateway;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.baseline.Baseline;
import org.hypertrace.graphql.metric.schema.Health;
import org.hypertrace.graphql.metric.schema.MetricBaselineAggregation;

import java.util.Optional;

class MetricBaselineAggregationConverter
    implements Converter<Optional<Baseline>, MetricBaselineAggregation> {

  @Override
  public Single<MetricBaselineAggregation> convert(Optional<Baseline> baseline) {
    return Single.just(getBaselinedMetricAggregation(baseline));
  }

  private MetricBaselineAggregation getBaselinedMetricAggregation(Optional<Baseline> baseline) {
    return new MetricBaselineAggregation() {
      @Override
      public Double lowerBound() {
        if (baseline.isPresent()) {
          return baseline.get().getLowerBound().getDouble();
        }
        return Double.valueOf(0);
      }

      @Override
      public Double upperBound() {
        if (baseline.isPresent()) {
          return baseline.get().getUpperBound().getDouble();
        }
        return Double.valueOf(0);
      }

      @Override
      public Health health() {
        return null;
      }

      @Override
      public Double value() {
        if (baseline.isPresent()) {
          return baseline.get().getValue().getDouble();
        }
        return Double.valueOf(0);
      }
    };
  }
}
