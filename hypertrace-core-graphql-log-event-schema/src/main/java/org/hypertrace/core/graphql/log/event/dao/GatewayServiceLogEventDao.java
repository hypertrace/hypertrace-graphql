package org.hypertrace.core.graphql.log.event.dao;

import static java.util.concurrent.TimeUnit.SECONDS;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.log.event.request.LogEventRequest;
import org.hypertrace.core.graphql.log.event.schema.LogEventResultSet;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.gateway.service.GatewayServiceGrpc;
import org.hypertrace.gateway.service.GatewayServiceGrpc.GatewayServiceFutureStub;
import org.hypertrace.gateway.service.v1.log.events.LogEventsRequest;
import org.hypertrace.gateway.service.v1.log.events.LogEventsResponse;

@Singleton
class GatewayServiceLogEventDao implements LogEventDao {
  private static final int DEFAULT_DEADLINE_SEC = 10;
  private final GatewayServiceFutureStub gatewayServiceStub;
  private final GrpcContextBuilder grpcContextBuilder;
  private final GatewayServiceLogEventsRequestBuilder requestBuilder;
  private final GatewayServiceLogEventsResponseConverter logEventConverter;

  @Inject
  GatewayServiceLogEventDao(
      GraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GrpcContextBuilder grpcContextBuilder,
      GrpcChannelRegistry channelRegistry,
      GatewayServiceLogEventsRequestBuilder requestBuilder,
      GatewayServiceLogEventsResponseConverter logEventConverter) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.requestBuilder = requestBuilder;
    this.logEventConverter = logEventConverter;

    this.gatewayServiceStub =
        GatewayServiceGrpc.newFutureStub(
                channelRegistry.forAddress(
                    serviceConfig.getGatewayServiceHost(), serviceConfig.getGatewayServicePort()))
            .withCallCredentials(credentials);
  }

  @Override
  public Single<LogEventResultSet> getLogEvents(LogEventRequest request) {
    return this.requestBuilder
        .buildRequest(request)
        .flatMap(serverRequest -> this.makeRequest(request.context(), serverRequest))
        .flatMap(serverResponse -> this.logEventConverter.convert(request, serverResponse));
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
