package org.hypertrace.core.graphql.trace.dao;

import static java.util.concurrent.TimeUnit.SECONDS;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.trace.request.TraceRequest;
import org.hypertrace.core.graphql.trace.schema.TraceResultSet;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.gateway.service.GatewayServiceGrpc;
import org.hypertrace.gateway.service.GatewayServiceGrpc.GatewayServiceFutureStub;
import org.hypertrace.gateway.service.v1.trace.TracesRequest;
import org.hypertrace.gateway.service.v1.trace.TracesResponse;

@Singleton
class GatewayServiceTraceDao implements TraceDao {
  private static final int DEFAULT_DEADLINE_SEC = 10;
  private final GatewayServiceFutureStub gatewayServiceStub;
  private final GrpcContextBuilder grpcContextBuilder;
  private final GatewayServiceTraceRequestBuilder requestBuilder;
  private final GatewayServiceTraceConverter traceConverter;

  @Inject
  GatewayServiceTraceDao(
      GraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GrpcContextBuilder grpcContextBuilder,
      GrpcChannelRegistry channelRegistry,
      GatewayServiceTraceRequestBuilder requestBuilder,
      GatewayServiceTraceConverter traceConverter) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.requestBuilder = requestBuilder;
    this.traceConverter = traceConverter;

    this.gatewayServiceStub =
        GatewayServiceGrpc.newFutureStub(
                channelRegistry.forAddress(
                    serviceConfig.getGatewayServiceHost(), serviceConfig.getGatewayServicePort()))
            .withCallCredentials(credentials);
  }

  @Override
  public Single<TraceResultSet> getTraces(TraceRequest request) {
    return this.requestBuilder
        .buildRequest(request)
        .flatMap(serverRequest -> this.makeRequest(request.context(), serverRequest))
        .flatMap(
            serverResponse ->
                this.traceConverter.convert(request.resultSetRequest(), serverResponse));
  }

  private Single<TracesResponse> makeRequest(GraphQlRequestContext context, TracesRequest request) {
    return Single.fromFuture(
        this.grpcContextBuilder
            .build(context)
            .call(
                () ->
                    this.gatewayServiceStub
                        .withDeadlineAfter(DEFAULT_DEADLINE_SEC, SECONDS)
                        .getTraces(request)));
  }
}
