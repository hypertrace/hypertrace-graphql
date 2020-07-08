package org.hypertrace.core.graphql.common.utils.attributes;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;
import org.hypertrace.core.graphql.common.utils.Converter;

class MetricAggregationTypeConverter
    implements Converter<AttributeModelMetricAggregationType, MetricAggregationType> {

  @Override
  public Single<MetricAggregationType> convert(
      AttributeModelMetricAggregationType aggregationType) {
    switch (aggregationType) {
      case COUNT:
        return Single.just(MetricAggregationType.COUNT);
      case AVG:
        return Single.just(MetricAggregationType.AVG);
      case SUM:
        return Single.just(MetricAggregationType.SUM);
      case MIN:
        return Single.just(MetricAggregationType.MIN);
      case MAX:
        return Single.just(MetricAggregationType.MAX);
      case AVGRATE:
        return Single.just(MetricAggregationType.AVGRATE);
      case PERCENTILE:
        return Single.just(MetricAggregationType.PERCENTILE);
      case DISTINCT_COUNT:
        return Single.just(MetricAggregationType.DISTINCTCOUNT);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format(
                    "Unrecognized attribute metric aggregation type %s", aggregationType.name())));
    }
  }
}
