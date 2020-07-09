package org.hypertrace.core.graphql.span.dao;

import static java.util.concurrent.TimeUnit.SECONDS;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.span.schema.SpanResultSet;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GraphQlGrpcContextBuilder;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.gateway.service.GatewayServiceGrpc;
import org.hypertrace.gateway.service.GatewayServiceGrpc.GatewayServiceFutureStub;
import org.hypertrace.gateway.service.v1.span.SpansRequest;
import org.hypertrace.gateway.service.v1.span.SpansResponse;

@Singleton
class GatewayServiceSpanDao implements SpanDao {
  private static final int DEFAULT_DEADLINE_SEC = 10;
  private final GatewayServiceFutureStub gatewayServiceStub;
  private final GraphQlGrpcContextBuilder grpcContextBuilder;
  private final GatewayServiceSpanRequestBuilder requestBuilder;
  private final GatewayServiceSpanConverter spanConverter;

  @Inject
  GatewayServiceSpanDao(
      GraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GraphQlGrpcContextBuilder grpcContextBuilder,
      GrpcChannelRegistry channelRegistry,
      GatewayServiceSpanRequestBuilder requestBuilder,
      GatewayServiceSpanConverter spanConverter) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.requestBuilder = requestBuilder;
    this.spanConverter = spanConverter;

    this.gatewayServiceStub =
        GatewayServiceGrpc.newFutureStub(
                channelRegistry.forAddress(
                    serviceConfig.getGatewayServiceHost(), serviceConfig.getGatewayServicePort()))
            .withCallCredentials(credentials);
  }

  @Override
  public Single<SpanResultSet> getSpans(ResultSetRequest<OrderArgument> request) {
    return this.requestBuilder
        .buildRequest(request)
        .flatMap(serverRequest -> this.makeRequest(request.context(), serverRequest))
        .flatMap(serverResponse -> this.spanConverter.convert(request, serverResponse));
  }

  private Single<SpansResponse> makeRequest(GraphQlRequestContext context, SpansRequest request) {
    return Single.fromFuture(
        this.grpcContextBuilder
            .build(context)
            .callInContext(
                () ->
                    this.gatewayServiceStub
                        .withDeadlineAfter(DEFAULT_DEADLINE_SEC, SECONDS)
                        .getSpans(request)));
  }
}
