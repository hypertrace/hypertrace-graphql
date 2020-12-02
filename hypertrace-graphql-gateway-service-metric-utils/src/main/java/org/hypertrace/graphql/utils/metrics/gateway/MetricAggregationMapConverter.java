package org.hypertrace.graphql.utils.metrics.gateway;

import static org.hypertrace.core.graphql.common.utils.CollectorUtils.immutableMapEntryCollector;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.gateway.service.v1.common.AggregatedMetricValue;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.schema.BaselineMetricAggregation;

class MetricAggregationMapConverter
    implements BiConverter<
        List<MetricAggregationRequest>,
        Map<String, AggregatedMetricValue>,
        Map<MetricLookupMapKey, BaselineMetricAggregation>> {

  private final MetricAggregationConverter aggregationConverter;

  @Inject
  MetricAggregationMapConverter(MetricAggregationConverter aggregationConverter) {
    this.aggregationConverter = aggregationConverter;
  }

  @Override
  public Single<Map<MetricLookupMapKey, BaselineMetricAggregation>> convert(
          List<MetricAggregationRequest> aggregationRequests,
          Map<String, AggregatedMetricValue> resultMap) {
    return Observable.fromIterable(aggregationRequests)
            .flatMapSingle(
                    aggregationRequest -> this.buildMetricAggregationEntry(aggregationRequest, resultMap))
            .collect(immutableMapEntryCollector());
  }

  private Single<Entry<MetricLookupMapKey, BaselineMetricAggregation>> buildMetricAggregationEntry(
      MetricAggregationRequest aggregationRequest, Map<String, AggregatedMetricValue> resultMap) {
    return this.aggregationConverter
        .convert(resultMap.get(aggregationRequest.alias()).getValue())
        .map(agg -> Map.entry(MetricLookupMapKey.fromAggregationRequest(aggregationRequest), agg));
  }
}
