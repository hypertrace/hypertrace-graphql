package org.hypertrace.graphql.utils.metrics.gateway;

import static io.reactivex.rxjava3.core.Single.zip;
import static org.hypertrace.core.graphql.common.utils.CollectorUtils.immutableMapEntryCollector;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observables.GroupedObservable;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.common.schema.time.TimeUnit;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.common.utils.CollectorUtils;
import org.hypertrace.gateway.service.v1.entity.Entity;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.request.MetricRequest;
import org.hypertrace.graphql.metric.request.MetricSeriesRequest;
import org.hypertrace.graphql.metric.schema.BaselineMetricInterval;
import org.hypertrace.graphql.metric.schema.BaselinedMetricAggregation;
import org.hypertrace.graphql.metric.schema.MetricContainer;
import org.hypertrace.graphql.metric.schema.MetricInterval;

class MetricContainerMapConverter
    implements BiConverter<Collection<MetricRequest>, Entity, Map<String, MetricContainer>> {

  private final MetricAggregationMapConverter aggregationMapConverter;
  private final MetricSeriesMapConverter seriesMapConverter;

  @Inject
  MetricContainerMapConverter(
      MetricAggregationMapConverter aggregationMapConverter,
      MetricSeriesMapConverter seriesMapConverter) {
    this.aggregationMapConverter = aggregationMapConverter;
    this.seriesMapConverter = seriesMapConverter;
  }

  @Override
  public Single<Map<String, MetricContainer>> convert(
      Collection<MetricRequest> metricRequests, Entity entity) {
    return Observable.fromIterable(metricRequests)
        .distinct()
        .groupBy(MetricRequest::attribute)
        .flatMapSingle(requests -> this.buildMetricContainerEntry(requests, entity))
        .collect(immutableMapEntryCollector());
  }

  private Single<Entry<String, MetricContainer>> buildMetricContainerEntry(
      GroupedObservable<AttributeModel, MetricRequest> requestsForAttribute, Entity entity) {

    return requestsForAttribute
        .collect(Collectors.toUnmodifiableList())
        .flatMap(metricRequests -> this.buildMetricContainerForAttribute(metricRequests, entity))
        .map(container -> Map.entry(requestsForAttribute.getKey().key(), container));
  }

  private Single<MetricContainer> buildMetricContainerForAttribute(
      List<MetricRequest> metricRequests, Entity entity) {
    List<MetricAggregationRequest> aggregationRequests =
        metricRequests.stream().collect(CollectorUtils.flatten(MetricRequest::aggregationRequests));

    List<MetricSeriesRequest> seriesRequests =
        metricRequests.stream().collect(CollectorUtils.flatten(MetricRequest::seriesRequests));
    return zip(
        this.aggregationMapConverter.convert(aggregationRequests, entity.getMetricMap()),
        this.seriesMapConverter.convert(seriesRequests, entity.getMetricSeriesMap()),
        BaselineConvertedAggregationContainerImpl::new);
  }

  private static class BaselineConvertedAggregationContainerImpl extends BaselineConvertedAggregationContainer
      implements MetricContainer {

    private final Map<Duration, List<MetricInterval>> metricSeriesMap;

    BaselineConvertedAggregationContainerImpl(
        Map<MetricLookupMapKey, BaselinedMetricAggregation> metricAggregationMap,
        Map<Duration, List<MetricInterval>> metricSeriesMap) {
      super(metricAggregationMap);
      this.metricSeriesMap = metricSeriesMap;
    }

    @Override
    public List<MetricInterval> series(int size, TimeUnit units) {
      return this.metricSeriesMap.get(Duration.of(size, units.getChronoUnit()));
    }

    @Override
    public List<BaselineMetricInterval> baselineSeries(int size, TimeUnit units) {
      // TODO implement this
      return Collections.EMPTY_LIST;
    }
  }
}
