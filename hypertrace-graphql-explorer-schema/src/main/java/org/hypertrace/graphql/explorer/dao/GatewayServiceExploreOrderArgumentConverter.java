package org.hypertrace.graphql.explorer.dao;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderDirection;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.ColumnIdentifier;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.OrderByExpression;
import org.hypertrace.gateway.service.v1.common.SortOrder;
import org.hypertrace.gateway.service.v1.explore.ColumnName;
import org.hypertrace.graphql.explorer.request.ExploreOrderArgument;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;

@Slf4j
class GatewayServiceExploreOrderArgumentConverter
    implements Converter<List<ExploreOrderArgument>, List<OrderByExpression>> {

  private final Converter<
          List<AttributeAssociation<AggregatableOrderArgument>>, List<OrderByExpression>>
      attributeOrderConverter;
  private final Converter<OrderDirection, SortOrder> sortOrderConverter;

  @Inject
  GatewayServiceExploreOrderArgumentConverter(
      Converter<List<AttributeAssociation<AggregatableOrderArgument>>, List<OrderByExpression>>
          attributeOrderConverter,
      Converter<OrderDirection, SortOrder> sortOrderConverter) {
    this.attributeOrderConverter = attributeOrderConverter;
    this.sortOrderConverter = sortOrderConverter;
  }

  @Override
  public Single<List<OrderByExpression>> convert(List<ExploreOrderArgument> arguments) {
    return Observable.fromIterable(arguments)
        .concatMapMaybe(this::convert)
        .collect(Collectors.toUnmodifiableList());
  }

  private Maybe<OrderByExpression> convert(ExploreOrderArgument argument) {
    switch (argument.type()) {
      case ATTRIBUTE:
        return this.buildAttributeOrderExpression(argument);
      case INTERVAL_START:
        return this.buildIntervalStartOrderExpression(argument);
      default:
        log.error("Unrecognized order argument type: {}", argument);
        return Maybe.empty();
    }
  }

  private Maybe<OrderByExpression> buildAttributeOrderExpression(ExploreOrderArgument argument) {
    return Maybe.fromOptional(argument.attribute())
        .doOnComplete(() -> log.error("Attribute order argument missing attribute: {} ", argument))
        .flatMapSingle(
            attribute ->
                this.attributeOrderConverter.convert(
                    List.of(AttributeAssociation.of(attribute, argument.argument()))))
        .mapOptional(list -> list.stream().findFirst());
  }

  private Maybe<OrderByExpression> buildIntervalStartOrderExpression(
      ExploreOrderArgument argument) {
    return this.sortOrderConverter
        .convert(argument.argument().direction())
        .map(this::buildIntervalStartOrderExpression)
        .toMaybe();
  }

  private OrderByExpression buildIntervalStartOrderExpression(SortOrder sortOrder) {
    return OrderByExpression.newBuilder()
        .setOrder(sortOrder)
        .setExpression(
            Expression.newBuilder()
                .setColumnIdentifier(
                    ColumnIdentifier.newBuilder()
                        .setColumnName(ColumnName.INTERVAL_START_TIME.name())))
        .build();
  }
}
