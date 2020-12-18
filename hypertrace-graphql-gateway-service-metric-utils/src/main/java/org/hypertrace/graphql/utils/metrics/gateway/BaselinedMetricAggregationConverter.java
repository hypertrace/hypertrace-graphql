package org.hypertrace.graphql.utils.metrics.gateway;

import graphql.annotations.annotationTypes.GraphQLNonNull;
import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.AggregatedMetricValue;
import org.hypertrace.graphql.metric.schema.BaselinedMetricAggregation;
import org.hypertrace.graphql.metric.schema.Health;
import org.hypertrace.graphql.metric.schema.MetricBaselineAggregation;

public class BaselinedMetricAggregationConverter
    implements Converter<AggregatedMetricValue, BaselinedMetricAggregation> {
  @Override
  public Single<BaselinedMetricAggregation> convert(AggregatedMetricValue value) {
    BaselinedMetricAggregation baselinedMetricAggregation =
        new BaselinedMetricAggregation() {
          @Override
          public MetricBaselineAggregation baseline() {
            if (!value.hasBaseline()) {
              // change this
              return null;
            }
            MetricBaselineAggregation metricBaselineAggregation =
                new MetricBaselineAggregation() {
                  @Override
                  public Double lowerBound() {
                    return value.getBaseline().getLowerBound().getDouble();
                  }

                  @Override
                  public Double upperBound() {
                    return value.getBaseline().getUpperBound().getDouble();
                  }

                  @Override
                  public Health health() {
                    return new Health() {

                      @Override
                      public @GraphQLNonNull Double value() {
                        return value.getBaseline().getHealth().getValue().getDouble();
                      }

                      @Override
                      public @GraphQLNonNull String message() {
                        if (value.getBaseline().getHealth().getValue().getDouble() == 1) {
                          return "Healthy";
                        } else {
                          return "Unhealthy";
                        }
                      }
                    };
                  }

                  @Override
                  public Double value() {
                    return null;
                  }
                };
            return metricBaselineAggregation;
          }

          @Override
          public Double value() {
            return value.getValue().getDouble();
          }
        };
    return Single.just(baselinedMetricAggregation);
  }
}
