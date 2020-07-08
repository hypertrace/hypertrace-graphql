package org.hypertrace.core.graphql.utils.gateway;

import static io.reactivex.rxjava3.core.Single.zip;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.OrderByExpression;

class OrderByExpressionListConverter
    implements Converter<List<AttributeAssociation<OrderArgument>>, List<OrderByExpression>> {

  private final ColumnIdentifierExpressionConverter columnExpressionConverter;
  private final SortOrderConverter sortOrderConverter;

  @Inject
  OrderByExpressionListConverter(
      ColumnIdentifierExpressionConverter columnExpressionConverter,
      SortOrderConverter sortOrderConverter) {
    this.columnExpressionConverter = columnExpressionConverter;
    this.sortOrderConverter = sortOrderConverter;
  }

  @Override
  public Single<List<OrderByExpression>> convert(List<AttributeAssociation<OrderArgument>> orders) {
    return Observable.fromIterable(orders)
        .flatMapSingle(this::buildOrderByExpression)
        .collect(Collectors.toUnmodifiableList());
  }

  private Single<OrderByExpression> buildOrderByExpression(
      AttributeAssociation<OrderArgument> orderArgument) {
    return zip(
        this.sortOrderConverter.convert(orderArgument.value().direction()),
        this.columnExpressionConverter.convert(orderArgument.attribute()),
        (sortOrder, columnExpression) ->
            OrderByExpression.newBuilder()
                .setOrder(sortOrder)
                .setExpression(columnExpression)
                .build());
  }
}
