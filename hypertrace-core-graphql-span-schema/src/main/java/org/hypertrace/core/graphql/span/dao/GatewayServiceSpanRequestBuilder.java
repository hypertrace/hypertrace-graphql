package org.hypertrace.core.graphql.span.dao;

import static io.reactivex.rxjava3.core.Single.zip;

import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.Filter;
import org.hypertrace.gateway.service.v1.common.OrderByExpression;
import org.hypertrace.gateway.service.v1.span.SpansRequest;

class GatewayServiceSpanRequestBuilder {

  private final Converter<Collection<AttributeAssociation<FilterArgument>>, Filter> filterConverter;
  private final Converter<List<AttributeAssociation<OrderArgument>>, List<OrderByExpression>>
      orderConverter;
  private final Converter<Collection<AttributeRequest>, Set<Expression>> attributeConverter;

  @Inject
  GatewayServiceSpanRequestBuilder(
      Converter<Collection<AttributeAssociation<FilterArgument>>, Filter> filterConverter,
      Converter<List<AttributeAssociation<OrderArgument>>, List<OrderByExpression>> orderConverter,
      Converter<Collection<AttributeRequest>, Set<Expression>> attributeConverter) {
    this.filterConverter = filterConverter;
    this.orderConverter = orderConverter;
    this.attributeConverter = attributeConverter;
  }

  Single<SpansRequest> buildRequest(ResultSetRequest<OrderArgument> gqlRequest) {
    return zip(
        this.attributeConverter.convert(gqlRequest.attributes()),
        this.orderConverter.convert(gqlRequest.orderArguments()),
        this.filterConverter.convert(gqlRequest.filterArguments()),
        (selections, orderBys, filters) ->
            SpansRequest.newBuilder()
                .setStartTimeMillis(gqlRequest.timeRange().startTime().toEpochMilli())
                .setEndTimeMillis(gqlRequest.timeRange().endTime().toEpochMilli())
                .addAllSelection(selections)
                .addAllOrderBy(orderBys)
                .setLimit(gqlRequest.limit())
                .setOffset(gqlRequest.offset())
                .setFilter(filters)
                .setSpaceId(gqlRequest.spaceId().orElse("")) // String proto default value
                .build());
  }
}
