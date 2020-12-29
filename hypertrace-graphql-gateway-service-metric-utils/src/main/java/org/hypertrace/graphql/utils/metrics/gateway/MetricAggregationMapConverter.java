package org.hypertrace.graphql.utils.metrics.gateway;

import static io.reactivex.rxjava3.core.Single.zip;
import static org.hypertrace.core.graphql.common.utils.CollectorUtils.immutableMapEntryCollector;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.utils.TriConverter;
import org.hypertrace.gateway.service.v1.baseline.Baseline;
import org.hypertrace.gateway.service.v1.common.AggregatedMetricValue;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.schema.BaselinedMetricAggregation;
import org.hypertrace.graphql.metric.schema.Health;
import org.hypertrace.graphql.metric.schema.MetricBaselineAggregation;

class MetricAggregationMapConverter
    implements TriConverter<
        List<MetricAggregationRequest>,
        Map<String, AggregatedMetricValue>,
        Map<String, Baseline>,
        Map<MetricLookupMapKey, BaselinedMetricAggregation>> {

  private final MetricAggregationConverter aggregationConverter;
  private final MetricBaselineAggregationConverter baselineAggregationConverter;

  @Inject
  MetricAggregationMapConverter(
      MetricAggregationConverter aggregationConverter,
      MetricBaselineAggregationConverter baselineAggregationConverter) {
    this.aggregationConverter = aggregationConverter;
    this.baselineAggregationConverter = baselineAggregationConverter;
  }

  @Override
  public Single<Map<MetricLookupMapKey, BaselinedMetricAggregation>> convert(
      List<MetricAggregationRequest> aggregationRequests,
      Map<String, AggregatedMetricValue> resultMap,
      Map<String, Baseline> baselineMap) {
    return Observable.fromIterable(aggregationRequests)
        .flatMapSingle(
            aggregationRequest ->
                this.buildMetricAggregationEntry(aggregationRequest, resultMap, baselineMap))
        .collect(immutableMapEntryCollector());
  }

  private Single<Entry<MetricLookupMapKey, BaselinedMetricAggregation>> buildMetricAggregationEntry(
      MetricAggregationRequest aggregationRequest,
      Map<String, AggregatedMetricValue> resultMap,
      Map<String, Baseline> baselineMap) {
    return zip(
            this.aggregationConverter.convert(resultMap.get(aggregationRequest.alias()).getValue()),
            this.baselineAggregationConverter.convert(
                Optional.ofNullable(baselineMap.get(aggregationRequest.alias()))),
            (metricAggregation, baselinedMetricAggregation) ->
                new BaselinedMetricAggregation() {
                  @Override
                  public Double value() {
                    return metricAggregation.value();
                  }

                  @Override
                  public MetricBaselineAggregation baseline() {
                    return new MetricBaselineAggregation() {

                      @Override
                      public Double value() {
                        return baselinedMetricAggregation.value();
                      }

                      @Override
                      public Double lowerBound() {
                        return baselinedMetricAggregation.lowerBound();
                      }

                      @Override
                      public Double upperBound() {
                        return baselinedMetricAggregation.upperBound();
                      }

                      @Override
                      public Health health() {
                        return baselinedMetricAggregation.health();
                      }
                    };
                  }
                })
        .map(agg -> Map.entry(MetricLookupMapKey.fromAggregationRequest(aggregationRequest), agg));
  }
}
