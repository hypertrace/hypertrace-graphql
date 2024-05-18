package org.hypertrace.graphql.utils.metrics.gateway;

import static io.reactivex.rxjava3.core.Single.zip;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderDirection;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.OrderByExpression;
import org.hypertrace.gateway.service.v1.common.SortOrder;
import org.hypertrace.graphql.metric.request.MetricAggregationRequestBuilder;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;

class AggregatableOrderByExpressionListConverter
    implements Converter<
        List<AttributeAssociation<AggregatableOrderArgument>>, List<OrderByExpression>> {

  private final Converter<AttributeAssociation<AttributeExpression>, Expression>
      columnExpressionConverter;
  private final MetricAggregationExpressionConverter metricAggregationExpressionConverter;
  private final Converter<OrderDirection, SortOrder> sortOrderConverter;
  private final Converter<MetricAggregationType, AttributeModelMetricAggregationType>
      aggregationTypeConverter;
  private final MetricAggregationRequestBuilder metricAggregationRequestBuilder;

  @Inject
  AggregatableOrderByExpressionListConverter(
      Converter<AttributeAssociation<AttributeExpression>, Expression> columnExpressionConverter,
      MetricAggregationExpressionConverter metricAggregationExpressionConverter,
      Converter<OrderDirection, SortOrder> sortOrderConverter,
      Converter<MetricAggregationType, AttributeModelMetricAggregationType>
          aggregationTypeConverter,
      MetricAggregationRequestBuilder metricAggregationRequestBuilder) {
    this.columnExpressionConverter = columnExpressionConverter;
    this.metricAggregationExpressionConverter = metricAggregationExpressionConverter;
    this.sortOrderConverter = sortOrderConverter;
    this.aggregationTypeConverter = aggregationTypeConverter;
    this.metricAggregationRequestBuilder = metricAggregationRequestBuilder;
  }

  @Override
  public Single<List<OrderByExpression>> convert(
      List<AttributeAssociation<AggregatableOrderArgument>> orders) {
    return Observable.fromIterable(orders)
        .flatMapSingle(this::buildOrderByExpression)
        .collect(Collectors.toUnmodifiableList());
  }

  private Single<OrderByExpression> buildOrderByExpression(
      AttributeAssociation<AggregatableOrderArgument> orderArgument) {
    return zip(
        this.sortOrderConverter.convert(orderArgument.value().direction()),
        this.buildSelectionExpression(orderArgument),
        (sortOrder, expression) ->
            OrderByExpression.newBuilder().setOrder(sortOrder).setExpression(expression).build());
  }

  private Single<Expression> buildSelectionExpression(
      AttributeAssociation<AggregatableOrderArgument> orderArgument) {
    AttributeAssociation<AttributeExpression> attributeExpressionAssociation =
        this.buildAttributeExpressionAssociation(orderArgument);
    List<Object> aggregationArguments =
        Optional.<Object>ofNullable(orderArgument.value().size())
            .map(List::of)
            .orElseGet(Collections::emptyList);
    return Maybe.fromOptional(Optional.ofNullable(orderArgument.value().aggregation()))
        .flatMapSingle(this.aggregationTypeConverter::convert)
        .map(
            aggregationType ->
                this.metricAggregationRequestBuilder.build(
                    attributeExpressionAssociation, aggregationType, aggregationArguments))
        .flatMapSingle(
            aggregationRequest ->
                this.metricAggregationExpressionConverter.convert(
                    aggregationRequest, aggregationRequest.alias()))
        .switchIfEmpty(this.columnExpressionConverter.convert(attributeExpressionAssociation));
  }

  private AttributeAssociation<AttributeExpression> buildAttributeExpressionAssociation(
      AttributeAssociation<AggregatableOrderArgument> orderArgumentAttributeAssociation) {
    return AttributeAssociation.of(
        orderArgumentAttributeAssociation.attribute(),
        orderArgumentAttributeAssociation.value().resolvedKeyExpression());
  }
}
