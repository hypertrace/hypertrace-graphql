package org.hypertrace.core.graphql.span.dao;

import static java.util.concurrent.TimeUnit.SECONDS;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.span.request.SpanRequest;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.gateway.service.GatewayServiceGrpc.GatewayServiceFutureStub;
import org.hypertrace.gateway.service.v1.log.events.LogEventsRequest;
import org.hypertrace.gateway.service.v1.log.events.LogEventsResponse;
import org.hypertrace.gateway.service.v1.span.SpansResponse;

class SpanLogEventDao {

  private static final int DEFAULT_DEADLINE_SEC = 10;

  private final GatewayServiceFutureStub gatewayServiceStub;
  private final GrpcContextBuilder grpcContextBuilder;
  private final SpanLogEventRequestBuilder spanLogEventRequestBuilder;
  private final SpanLogEventResponseConverter spanLogEventResponseConverter;

  @Inject
  SpanLogEventDao(
      GatewayServiceFutureStub gatewayServiceFutureStub,
      GrpcContextBuilder grpcContextBuilder,
      SpanLogEventRequestBuilder spanLogEventRequestBuilder,
      SpanLogEventResponseConverter spanLogEventResponseConverter) {
    this.gatewayServiceStub = gatewayServiceFutureStub;
    this.grpcContextBuilder = grpcContextBuilder;
    this.spanLogEventRequestBuilder = spanLogEventRequestBuilder;
    this.spanLogEventResponseConverter = spanLogEventResponseConverter;
  }

  /**
   *
   *
   * <ul>
   *   <li>1. Fetch log event attributes from {@code gqlRequest}
   *   <li>2. Build log event request using attribute and spanIds as filter
   *   <li>3. Query log events
   *   <li>4. Processed log events response to build mapping from spanId to logEvent
   * </ul>
   */
  Single<SpanLogEventsResponse> fetchLogEvents(
      SpanRequest gqlRequest, SpansResponse spansResponse) {
    if (null == gqlRequest.spanEventsRequest().idAttribute()
        || null == gqlRequest.logEventAttributes()
        || gqlRequest.logEventAttributes().isEmpty()) {
      return Single.just(new SpanLogEventsResponse(spansResponse, Map.of()));
    }
    return spanLogEventRequestBuilder
        .buildLogEventsRequest(gqlRequest, spansResponse)
        .flatMap(
            logEventsRequest ->
                makeRequest(gqlRequest.spanEventsRequest().context(), logEventsRequest))
        .flatMap(
            logEventsResponse ->
                spanLogEventResponseConverter.buildResponse(
                    gqlRequest.spanEventsRequest().context(),
                    gqlRequest.logEventAttributes(),
                    spansResponse,
                    logEventsResponse));
  }

  private Single<LogEventsResponse> makeRequest(
      GraphQlRequestContext context, LogEventsRequest request) {
    return Single.fromFuture(
        this.grpcContextBuilder
            .build(context)
            .call(
                () ->
                    this.gatewayServiceStub
                        .withDeadlineAfter(DEFAULT_DEADLINE_SEC, SECONDS)
                        .getLogEvents(request)));
  }
}
