package org.hypertrace.graphql.utils.metrics.gateway;

import static org.hypertrace.core.graphql.common.utils.CollectorUtils.immutableMapEntryCollector;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observables.GroupedObservable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.gateway.service.v1.common.AggregatedMetricValue;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.schema.BaselinedMetricAggregationContainer;

class MetricAggregationContainerMapConverter
    implements BiConverter<
        Collection<MetricAggregationRequest>,
        Map<String, AggregatedMetricValue>,
        Map<AttributeExpression, BaselinedMetricAggregationContainer>> {

  private final BaselinedMetricAggregationMapConverter aggregationMapConverter;

  @Inject
  MetricAggregationContainerMapConverter(
      BaselinedMetricAggregationMapConverter aggregationMapConverter) {
    this.aggregationMapConverter = aggregationMapConverter;
  }

  @Override
  public Single<Map<AttributeExpression, BaselinedMetricAggregationContainer>> convert(
      Collection<MetricAggregationRequest> metricRequests,
      Map<String, AggregatedMetricValue> metricResponses) {
    return Observable.fromIterable(metricRequests)
        .distinct()
        .groupBy(request -> request.attributeExpressionAssociation().value())
        .flatMapSingle(requests -> this.buildMetricContainerEntry(requests, metricResponses))
        .collect(immutableMapEntryCollector());
  }

  private Single<Entry<AttributeExpression, BaselinedMetricAggregationContainer>>
      buildMetricContainerEntry(
          GroupedObservable<AttributeExpression, MetricAggregationRequest> requestsForAttribute,
          Map<String, AggregatedMetricValue> metricResponses) {

    return requestsForAttribute
        .toList()
        .flatMap(
            metricRequests ->
                this.aggregationMapConverter.convert(
                    metricRequests, metricResponses, Collections.emptyMap()))
        .map(BaselinedConvertedAggregationContainer::new)
        .map(container -> Map.entry(requestsForAttribute.getKey(), container));
  }
}
