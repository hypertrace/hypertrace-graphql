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
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

class DefaultSpanRequestBuilder implements SpanRequestBuilder {

  private final ResultSetRequestBuilder resultSetRequestBuilder;
  private final LogEventAttributeRequestBuilder logEventAttributeRequestBuilder;

  @Inject
  public DefaultSpanRequestBuilder(
      ResultSetRequestBuilder resultSetRequestBuilder,
      LogEventAttributeRequestBuilder logEventAttributeRequestBuilder) {
    this.resultSetRequestBuilder = resultSetRequestBuilder;
    this.logEventAttributeRequestBuilder = logEventAttributeRequestBuilder;
  }

  @Override
  public Single<SpanRequest> build(
      GraphQlRequestContext context,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet) {
    return zip(
        resultSetRequestBuilder.build(
            context,
            HypertraceCoreAttributeScopeString.SPAN,
            arguments,
            selectionSet,
            OrderArgument.class),
        logEventAttributeRequestBuilder.buildAttributeRequest(context, selectionSet),
        (resultSetRequest, logEventAttributeRequest) ->
            new DefaultSpanRequest(context, resultSetRequest, logEventAttributeRequest));
  }

  @Override
  public Single<SpanRequest> build(
      GraphQlRequestContext context,
      Map<String, Object> arguments,
      List<String> spanAttributes,
      List<String> logAttributes) {
    return zip(
        resultSetRequestBuilder.build(
            context, HypertraceCoreAttributeScopeString.SPAN, arguments, spanAttributes),
        logEventAttributeRequestBuilder.buildAttributeRequest(context, logAttributes),
        (resultSetRequest, logEventAttributeRequest) ->
            new DefaultSpanRequest(context, resultSetRequest, logEventAttributeRequest));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultSpanRequest implements SpanRequest {
    GraphQlRequestContext context;
    ResultSetRequest<OrderArgument> spanEventsRequest;
    Collection<AttributeRequest> logEventAttributes;
  }
}
