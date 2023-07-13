package org.hypertrace.core.graphql.trace.request;

import graphql.schema.DataFetchingFieldSelectionSet;
import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequestBuilder;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.trace.schema.arguments.TraceType;
import org.hypertrace.core.graphql.trace.schema.arguments.TraceTypeArgument;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;

class DefaultTraceRequestBuilder implements TraceRequestBuilder {

  private final ResultSetRequestBuilder resultSetRequestBuilder;
  private final ArgumentDeserializer argumentDeserializer;
  private final GraphQlSelectionFinder selectionFinder;

  @Inject
  DefaultTraceRequestBuilder(
      ResultSetRequestBuilder resultSetRequestBuilder,
      ArgumentDeserializer argumentDeserializer,
      GraphQlSelectionFinder selectionFinder) {
    this.resultSetRequestBuilder = resultSetRequestBuilder;
    this.argumentDeserializer = argumentDeserializer;
    this.selectionFinder = selectionFinder;
  }

  @Override
  public Single<TraceRequest> build(
      GraphQlRequestContext context,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet) {

    TraceType traceType =
        this.argumentDeserializer
            .deserializePrimitive(arguments, TraceTypeArgument.class)
            .orElseThrow();

    boolean fetchTotal =
        this.selectionFinder
                .findSelections(
                    selectionSet, SelectionQuery.namedChild(ResultSet.RESULT_SET_TOTAL_NAME))
                .count()
            > 0;

    return this.build(context, traceType, arguments, selectionSet, fetchTotal);
  }

  private Single<TraceRequest> build(
      GraphQlRequestContext context,
      TraceType traceType,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet,
      boolean fetchTotal) {

    return this.resultSetRequestBuilder
        .build(context, traceType.getScopeString(), arguments, selectionSet)
        .map(
            resultSetRequest ->
                new DefaultTraceRequest(context, resultSetRequest, traceType, fetchTotal));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultTraceRequest implements TraceRequest {
    GraphQlRequestContext context;
    ResultSetRequest<OrderArgument> resultSetRequest;
    TraceType traceType;
    boolean fetchTotal;
  }
}
