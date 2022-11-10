package org.hypertrace.core.graphql.metadata.response;

import static io.reactivex.rxjava3.core.Single.zip;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType;
import org.hypertrace.core.graphql.attributes.AttributeModelType;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeType;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeScopeStringTranslator;
import org.hypertrace.core.graphql.metadata.schema.AttributeMetadata;

public class MetadataResponseBuilder {

  private final Converter<AttributeModelType, AttributeType> typeConverter;
  private final Converter<AttributeModelMetricAggregationType, MetricAggregationType>
      aggregationTypeConverter;
  private final AttributeScopeStringTranslator scopeStringTranslator;

  @Inject
  MetadataResponseBuilder(
      Converter<AttributeModelType, AttributeType> typeConverter,
      Converter<AttributeModelMetricAggregationType, MetricAggregationType>
          aggregationTypeConverter,
      AttributeScopeStringTranslator scopeStringTranslator) {
    this.typeConverter = typeConverter;
    this.aggregationTypeConverter = aggregationTypeConverter;
    this.scopeStringTranslator = scopeStringTranslator;
  }

  public Single<List<AttributeMetadata>> build(List<AttributeModel> modelList) {
    return Observable.fromIterable(modelList)
        .flatMapMaybe(this::build)
        .collect(Collectors.toUnmodifiableList());
  }

  public Maybe<AttributeMetadata> build(AttributeModel model) {
    return zip(
            this.convertMetricAggregationTypes(model.supportedMetricAggregationTypes()),
            this.typeConverter.convert(model.type()),
            (aggregations, type) ->
                new DefaultAttributeMetadata(
                    this.scopeStringTranslator.toExternal(model.scope()),
                    model.key(),
                    model.displayName(),
                    type,
                    model.units(),
                    model.onlySupportsGrouping(),
                    model.onlySupportsAggregation(),
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
