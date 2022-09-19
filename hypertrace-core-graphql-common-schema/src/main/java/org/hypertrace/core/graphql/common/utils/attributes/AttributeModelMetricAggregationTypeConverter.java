package org.hypertrace.core.graphql.common.utils.attributes;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;
import org.hypertrace.core.graphql.common.utils.Converter;

class AttributeModelMetricAggregationTypeConverter
    implements Converter<MetricAggregationType, AttributeModelMetricAggregationType> {

  @Override
  public Single<AttributeModelMetricAggregationType> convert(
      MetricAggregationType aggregationType) {
    switch (aggregationType) {
      case COUNT:
        return Single.just(AttributeModelMetricAggregationType.COUNT);
      case AVG:
        return Single.just(AttributeModelMetricAggregationType.AVG);
      case SUM:
        return Single.just(AttributeModelMetricAggregationType.SUM);
      case MIN:
        return Single.just(AttributeModelMetricAggregationType.MIN);
      case MAX:
        return Single.just(AttributeModelMetricAggregationType.MAX);
      case AVGRATE:
        return Single.just(AttributeModelMetricAggregationType.AVGRATE);
      case PERCENTILE:
        return Single.just(AttributeModelMetricAggregationType.PERCENTILE);
      case DISTINCTCOUNT:
        return Single.just(AttributeModelMetricAggregationType.DISTINCT_COUNT);
      case DISTINCT_ARRAY:
        return Single.just(AttributeModelMetricAggregationType.DISTINCT_ARRAY);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format(
                    "Unrecognized attribute metric aggregation type %s", aggregationType.name())));
    }
  }
}
