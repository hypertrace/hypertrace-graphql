package org.hypertrace.graphql.utils.metrics.gateway;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observables.GroupedObservable;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.utils.CollectorUtils;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.baseline.Baseline;
import org.hypertrace.gateway.service.v1.baseline.BaselineInterval;
import org.hypertrace.gateway.service.v1.baseline.BaselineMetricSeries;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.request.MetricSeriesRequest;
import org.hypertrace.graphql.metric.schema.BaselineMetricInterval;
import org.hypertrace.graphql.metric.schema.BaselinedMetricAggregation;
import org.hypertrace.graphql.metric.schema.Health;
import org.hypertrace.graphql.metric.schema.MetricBaselineAggregation;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BaselineIntervalListContainer
    implements Converter<
        Map<MetricSeriesRequest, BaselineMetricSeries>, List<BaselineMetricInterval>> {

  private final MetricBaselineAggregationConverter baselineAggregationConverter;

  @Inject
  BaselineIntervalListContainer(MetricBaselineAggregationConverter baselineAggregationConverter) {
    this.baselineAggregationConverter = baselineAggregationConverter;
  }

  @Override
  public Single<List<BaselineMetricInterval>> convert(
      Map<MetricSeriesRequest, BaselineMetricSeries> metricSeriesMap) {
    return this.collectPartialsFromEachSeries(metricSeriesMap)
        .groupBy(BaselineIntervalListContainer.MergeableIntervalPartial::getIntervalTimeRange)
        .flatMapSingle(this::buildMetricIntervalFromPartials)
        .sorted(Comparator.comparing(BaselineMetricInterval::startTime))
        .collect(Collectors.toUnmodifiableList());
  }

  private Single<BaselineMetricInterval> buildMetricIntervalFromPartials(
      GroupedObservable<IntervalTimeRange, MergeableIntervalPartial> partialsForTimeRange) {
    return partialsForTimeRange
        .distinct()
        .collect(
            Collectors.groupingBy(
                partial ->
                    MetricLookupMapKey.fromAggregationRequest(partial.getAggregationRequest()),
                Collectors.mapping(
                    BaselineIntervalListContainer.MergeableIntervalPartial::getAggregation,
                    CollectorUtils.firstOrDefault(null))))
        .map(
            aggregationMap ->
                new BaselineIntervalListContainer.ConvertedMetricInterval(
                    aggregationMap, partialsForTimeRange.getKey()));
  }

  private Observable<BaselineIntervalListContainer.MergeableIntervalPartial>
      collectPartialsFromEachSeries(
          Map<MetricSeriesRequest, BaselineMetricSeries> metricSeriesMap) {
    return Observable.fromIterable(metricSeriesMap.entrySet())
        .flatMapStream(this::flattenSeriesEntry)
        .flatMapSingle(entry -> this.buildMergeablePartial(entry.getValue(), entry.getKey()));
  }

  private Stream<Map.Entry<MetricAggregationRequest, BaselineInterval>> flattenSeriesEntry(
      Map.Entry<MetricSeriesRequest, BaselineMetricSeries> entry) {
    return entry.getValue().getBaselineValueList().stream()
        .map(interval -> Map.entry(entry.getKey().aggregationRequest(), interval));
  }

  private Single<BaselineIntervalListContainer.MergeableIntervalPartial> buildMergeablePartial(
      BaselineInterval interval, MetricAggregationRequest aggregationRequest) {
    return this.baselineAggregationConverter
        .convert(Optional.ofNullable(interval.getBaseline()))
        .map(
            aggregation ->
                new BaselineIntervalListContainer.MergeableIntervalPartial(
                    this.extractIntervalTimeRange(interval), aggregationRequest, aggregation));
  }

  private BaselineIntervalListContainer.IntervalTimeRange extractIntervalTimeRange(
      BaselineInterval interval) {
    return new BaselineIntervalListContainer.IntervalTimeRange(
        Instant.ofEpochMilli(interval.getStartTimeMillis()),
        Instant.ofEpochMilli(interval.getEndTimeMillis()),
        getMetricbaselineAggregation(interval.getBaseline()));
  }

  private MetricBaselineAggregation getMetricbaselineAggregation(Baseline baseline) {
    return new MetricBaselineAggregation() {

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

      @Override
      public Health health() {
        // TODO modify this
        return null;
      }
    };
  }

  @Value
  private static class IntervalTimeRange {
    Instant startTime;
    Instant endTime;
    MetricBaselineAggregation metricBaselineAggregation;
  }

  @Value
  private static class MergeableIntervalPartial {
    BaselineIntervalListContainer.IntervalTimeRange intervalTimeRange;
    MetricAggregationRequest aggregationRequest;
    MetricBaselineAggregation aggregation;
  }

  @Getter
  @Accessors(fluent = true)
  private static class ConvertedMetricInterval extends BaselineConvertedAggregationContainer
      implements BaselineMetricInterval {
    final Instant startTime;
    final Instant endTime;

    ConvertedMetricInterval(
        Map<MetricLookupMapKey, MetricBaselineAggregation> metricAggregationMap,
        IntervalTimeRange intervalTimeRange) {
      super(getBaselineAggMap(metricAggregationMap));
      this.startTime = intervalTimeRange.getStartTime();
      this.endTime = intervalTimeRange.getEndTime();
    }

    // TODO Fix this
    private static Map<MetricLookupMapKey, BaselinedMetricAggregation> getBaselineAggMap(
        Map<MetricLookupMapKey, MetricBaselineAggregation> metricAggregationMap) {
      Map<MetricLookupMapKey, BaselinedMetricAggregation> baselineMap = new HashMap<>();
      metricAggregationMap.forEach(
          (key, value) -> {
            BaselinedMetricAggregation baselineAgg =
                new BaselinedMetricAggregation() {

                  @Override
                  public Double value() {
                    return null;
                  }

                  @Override
                  public MetricBaselineAggregation baseline() {
                    return value;
                  }
                };
            baselineMap.put(key, baselineAgg);
          });
      return baselineMap;
    }
  }
}
