package org.hypertrace.core.graphql.trace.dao;

import static io.reactivex.rxjava3.core.Single.zip;

import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.trace.request.TraceRequest;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.Filter;
import org.hypertrace.gateway.service.v1.common.OrderByExpression;
import org.hypertrace.gateway.service.v1.trace.TracesRequest;

class GatewayServiceTraceRequestBuilder {

  private final Converter<Collection<AttributeAssociation<FilterArgument>>, Filter> filterConverter;
  private final Converter<List<AttributeAssociation<OrderArgument>>, List<OrderByExpression>>
      orderConverter;
  private final Converter<Collection<AttributeRequest>, Set<Expression>> selectionConverter;

  @Inject
  GatewayServiceTraceRequestBuilder(
      Converter<Collection<AttributeAssociation<FilterArgument>>, Filter> filterConverter,
      Converter<List<AttributeAssociation<OrderArgument>>, List<OrderByExpression>> orderConverter,
      Converter<Collection<AttributeRequest>, Set<Expression>> selectionConverter) {
    this.filterConverter = filterConverter;
    this.orderConverter = orderConverter;
    this.selectionConverter = selectionConverter;
  }

  Single<TracesRequest> buildRequest(TraceRequest request) {

    return zip(
        this.selectionConverter.convert(request.resultSetRequest().attributes()),
        this.orderConverter.convert(request.resultSetRequest().orderArguments()),
        this.filterConverter.convert(request.resultSetRequest().filterArguments()),
        (selections, orderBys, filters) ->
            TracesRequest.newBuilder()
                .setScope(request.traceType().getScopeString())
                .setStartTimeMillis(
                    request.resultSetRequest().timeRange().startTime().toEpochMilli())
                .setEndTimeMillis(request.resultSetRequest().timeRange().endTime().toEpochMilli())
                .addAllSelection(selections)
                .addAllOrderBy(orderBys)
                .setLimit(request.resultSetRequest().limit())
                .setOffset(request.resultSetRequest().offset())
                .setFilter(filters)
                .build());
  }
}
