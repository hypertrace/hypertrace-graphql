package org.hypertrace.core.graphql.span.request;

import static io.reactivex.rxjava3.core.Single.zip;

import graphql.schema.DataFetchingFieldSelectionSet;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.atttributes.scopes.HypertraceCoreAttributeScopeString;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequestBuilder;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;

class DefaultSpanRequestBuilder implements SpanRequestBuilder {

  private final ResultSetRequestBuilder resultSetRequestBuilder;
  private final LogEventAttributeRequestBuilder logEventAttributeRequestBuilder;
  private final GraphQlSelectionFinder selectionFinder;

  @Inject
  public DefaultSpanRequestBuilder(
      ResultSetRequestBuilder resultSetRequestBuilder,
      LogEventAttributeRequestBuilder logEventAttributeRequestBuilder,
      GraphQlSelectionFinder selectionFinder) {
    this.resultSetRequestBuilder = resultSetRequestBuilder;
    this.logEventAttributeRequestBuilder = logEventAttributeRequestBuilder;
    this.selectionFinder = selectionFinder;
  }

  @Override
  public Single<SpanRequest> build(
      GraphQlRequestContext context,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet) {
    boolean fetchTotal =
        this.selectionFinder
                .findSelections(
                    selectionSet, SelectionQuery.namedChild(ResultSet.RESULT_SET_TOTAL_NAME))
                .count()
            > 0;

    return zip(
        resultSetRequestBuilder.build(
            context,
            HypertraceCoreAttributeScopeString.SPAN,
            arguments,
            selectionSet,
            OrderArgument.class),
        logEventAttributeRequestBuilder.buildAttributeRequest(context, selectionSet),
        (resultSetRequest, logEventAttributeRequest) ->
            new DefaultSpanRequest(
                context, resultSetRequest, logEventAttributeRequest, fetchTotal));
  }

  @Override
  public Single<SpanRequest> build(
      GraphQlRequestContext context,
      Map<String, Object> arguments,
      List<AttributeExpression> spanAttributeExpressions,
      List<AttributeExpression> logAttributeExpressions) {

    return zip(
        resultSetRequestBuilder.build(
            context, HypertraceCoreAttributeScopeString.SPAN, arguments, spanAttributeExpressions),
        logEventAttributeRequestBuilder.buildAttributeRequest(context, logAttributeExpressions),
        (resultSetRequest, logEventAttributeRequest) ->
            // This build method is utilized in the exportSpans API, which does not accept the total
            // parameter as an argument. Ref {@link ExportSpanResult}.
            // So, we explicitly set fetchTotal to false.
            new DefaultSpanRequest(context, resultSetRequest, logEventAttributeRequest, false));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultSpanRequest implements SpanRequest {
    GraphQlRequestContext context;
    ResultSetRequest<OrderArgument> spanEventsRequest;
    Collection<AttributeRequest> logEventAttributes;
    boolean fetchTotal;
  }
}
