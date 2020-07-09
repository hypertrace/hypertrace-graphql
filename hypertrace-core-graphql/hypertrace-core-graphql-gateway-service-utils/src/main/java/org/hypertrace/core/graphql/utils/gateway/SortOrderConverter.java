package org.hypertrace.core.graphql.utils.gateway;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderDirection;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.SortOrder;

class SortOrderConverter implements Converter<OrderDirection, SortOrder> {

  @Override
  public Single<SortOrder> convert(OrderDirection orderDirection) {
    switch (orderDirection) {
      case ASC:
        return Single.just(SortOrder.ASC);
      case DESC:
        return Single.just(SortOrder.DESC);
      default:
        return Single.error(
            new UnsupportedOperationException(
                String.format("Cannot convert sort order for unknown value '%s'", orderDirection)));
    }
  }
}
