package org.hypertrace.core.graphql.metadata.response;

import static io.reactivex.rxjava3.core.Single.zip;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType;
import org.hypertrace.core.graphql.attributes.AttributeModelType;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeType;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.metadata.schema.AttributeMetadata;

public class MetadataResponseBuilder {

  private final Converter<String, Optional<AttributeScope>> scopeConverter;
  private final Converter<AttributeModelType, AttributeType> typeConverter;
  private final Converter<AttributeModelMetricAggregationType, MetricAggregationType>
      aggregationTypeConverter;

  @Inject
  MetadataResponseBuilder(
      Converter<String, Optional<AttributeScope>> scopeConverter,
      Converter<AttributeModelType, AttributeType> typeConverter,
      Converter<AttributeModelMetricAggregationType, MetricAggregationType>
          aggregationTypeConverter) {
    this.scopeConverter = scopeConverter;
    this.typeConverter = typeConverter;
    this.aggregationTypeConverter = aggregationTypeConverter;
  }

  public Single<List<AttributeMetadata>> build(List<AttributeModel> modelList) {
    return Observable.fromIterable(modelList)
        .flatMapMaybe(this::build)
        .collect(Collectors.toUnmodifiableList());
  }

  private Maybe<AttributeMetadata> build(AttributeModel model) {
    return zip(
            this.scopeConverter.convert(model.scope()),
            this.convertMetricAggregationTypes(model.supportedMetricAggregationTypes()),
            this.typeConverter.convert(model.type()),
            (scopeEnum, aggregations, type) ->
                new DefaultAttributeMetadata(
                    scopeEnum.orElse(null),
                    scopeEnum.map(AttributeScope::getExternalScopeString).orElseGet(model::scope),
                    model.key(),
                    model.displayName(),
                    type,
                    model.units(),
                    model.requiresAggregation(),
                    aggregations,
                    model.groupable()))
        .cast(AttributeMetadata.class)
        .onErrorComplete();
  }

  private Single<List<MetricAggregationType>> convertMetricAggregationTypes(
      List<AttributeModelMetricAggregationType> aggregationTypes) {
    return Observable.fromIterable(aggregationTypes)
        .flatMapMaybe(
            aggregationType ->
                this.aggregationTypeConverter.convert(aggregationType).onErrorComplete())
        .collect(Collectors.toUnmodifiableList());
  }
}
