package org.hypertrace.graphql.utils.metrics.gateway;

import static io.reactivex.rxjava3.core.Single.zip;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.FunctionExpression;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;

class MetricAggregationExpressionConverter
    implements Converter<Collection<MetricAggregationRequest>, Set<Expression>> {
  private final Converter<AttributeAssociation<AttributeExpression>, Expression>
      columnExpressionConverter;
  private final FunctionTypeConverter functionTypeConverter;
  private final Converter<Object, Expression> literalConverter;

  @Inject
  MetricAggregationExpressionConverter(
      Converter<AttributeAssociation<AttributeExpression>, Expression> columnExpressionConverter,
      FunctionTypeConverter functionTypeConverter,
      Converter<Object, Expression> literalConverter) {
    this.columnExpressionConverter = columnExpressionConverter;
    this.functionTypeConverter = functionTypeConverter;
    this.literalConverter = literalConverter;
  }

  @Override
  public Single<Set<Expression>> convert(Collection<MetricAggregationRequest> metricAggregations) {
    return Observable.fromIterable(metricAggregations)
        .flatMapSingle(request -> this.convert(request, request.alias()))
        .collect(Collectors.toUnmodifiableSet());
  }

  Single<Expression> convert(MetricAggregationRequest metricAggregation, String alias) {
    return this.buildAggregationFunctionExpression(
            metricAggregation.attributeExpressionAssociation(),
            metricAggregation.aggregation(),
            metricAggregation.arguments(),
            alias)
        .map(functionExpression -> Expression.newBuilder().setFunction(functionExpression).build());
  }

  private Single<FunctionExpression> buildAggregationFunctionExpression(
      AttributeAssociation<AttributeExpression> attributeExpressionAssociation,
      AttributeModelMetricAggregationType aggregationType,
      List<Object> arguments,
      String alias) {
    return zip(
        functionTypeConverter.convert(aggregationType),
        this.columnExpressionConverter.convert(attributeExpressionAssociation),
        this.convertArguments(arguments),
        (functionType, columnExpression, argumentExpressionList) ->
            FunctionExpression.newBuilder()
                .setFunction(functionType)
                .setAlias(alias)
                .addArguments(columnExpression)
                .addAllArguments(argumentExpressionList)
                .build());
  }

  private Single<List<Expression>> convertArguments(List<Object> objectList) {
    return Observable.fromIterable(objectList)
        .flatMapSingle(this.literalConverter::convert)
        .collect(Collectors.toUnmodifiableList());
  }
}
