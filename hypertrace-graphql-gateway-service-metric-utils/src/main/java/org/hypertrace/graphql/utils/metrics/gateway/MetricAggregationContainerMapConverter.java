package org.hypertrace.graphql.utils.metrics.gateway;

import static org.hypertrace.core.graphql.common.utils.CollectorUtils.immutableMapEntryCollector;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observables.GroupedObservable;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.gateway.service.v1.common.AggregatedMetricValue;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.schema.MetricAggregationContainer;

class MetricAggregationContainerMapConverter
    implements BiConverter<
        Collection<MetricAggregationRequest>,
        Map<String, AggregatedMetricValue>,
        Map<String, MetricAggregationContainer>> {

  private final MetricAggregationMapConverter aggregationMapConverter;

  @Inject
  MetricAggregationContainerMapConverter(MetricAggregationMapConverter aggregationMapConverter) {
    this.aggregationMapConverter = aggregationMapConverter;
  }

  @Override
  public Single<Map<String, MetricAggregationContainer>> convert(
      Collection<MetricAggregationRequest> metricRequests,
      Map<String, AggregatedMetricValue> metricResponses) {
    return Observable.fromIterable(metricRequests)
        .distinct()
        .groupBy(MetricAggregationRequest::attribute)
        .flatMapSingle(requests -> this.buildMetricContainerEntry(requests, metricResponses))
        .collect(immutableMapEntryCollector());
  }

  private Single<Entry<String, MetricAggregationContainer>> buildMetricContainerEntry(
      GroupedObservable<AttributeModel, MetricAggregationRequest> requestsForAttribute,
      Map<String, AggregatedMetricValue> metricResponses) {

    return requestsForAttribute
        .toList()
        .flatMap(
            metricRequests -> this.aggregationMapConverter.convert(metricRequests, metricResponses))
        .map(ConvertedAggregationContainer::new)
        .map(container -> Map.entry(requestsForAttribute.getKey().key(), container));
  }
}
