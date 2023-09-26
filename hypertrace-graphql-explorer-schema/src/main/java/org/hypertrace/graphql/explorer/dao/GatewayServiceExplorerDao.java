package org.hypertrace.graphql.explorer.dao;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.request.transformation.RequestTransformer;
import org.hypertrace.core.graphql.rx.BoundedIoScheduler;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.gateway.service.GatewayServiceGrpc;
import org.hypertrace.gateway.service.GatewayServiceGrpc.GatewayServiceFutureStub;
import org.hypertrace.gateway.service.v1.explore.ExploreResponse;
import org.hypertrace.graphql.explorer.request.ExploreRequest;
import org.hypertrace.graphql.explorer.schema.ExploreResultSet;

@Slf4j
@Singleton
class GatewayServiceExplorerDao implements ExplorerDao {
  private final GatewayServiceFutureStub gatewayServiceStub;
  private final GrpcContextBuilder grpcContextBuilder;
  private final GatewayServiceExploreRequestBuilder requestBuilder;
  private final GatewayServiceExploreResponseConverter responseConverter;
  private final GraphQlServiceConfig serviceConfig;
  private final Scheduler boundedIoScheduler;
  private final RequestTransformer requestTransformer;

  @Inject
  GatewayServiceExplorerDao(
      GraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GrpcContextBuilder grpcContextBuilder,
      GrpcChannelRegistry grpcChannelRegistry,
      GatewayServiceExploreRequestBuilder requestBuilder,
      GatewayServiceExploreResponseConverter responseConverter,
      @BoundedIoScheduler Scheduler boundedIoScheduler,
      RequestTransformer requestTransformer) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.requestBuilder = requestBuilder;
    this.responseConverter = responseConverter;
    this.serviceConfig = serviceConfig;
    this.boundedIoScheduler = boundedIoScheduler;
    this.requestTransformer = requestTransformer;

    this.gatewayServiceStub =
        GatewayServiceGrpc.newFutureStub(
                grpcChannelRegistry.forAddress(
                    serviceConfig.getGatewayServiceHost(), serviceConfig.getGatewayServicePort()))
            .withCallCredentials(credentials);
  }

  @Override
  public Single<ExploreResultSet> explore(ExploreRequest request) {
    return this.requestTransformer
        .transform(request)
        .flatMap(this.requestBuilder::buildRequest)
        .subscribeOn(this.boundedIoScheduler)
        .flatMap(serverRequest -> this.makeRequest(request.context(), serverRequest))
        .flatMap(serverResponse -> this.responseConverter.convert(request, serverResponse))
        .doOnError(error -> log.error("Error while handling explore request {}", request, error));
  }

  private Single<ExploreResponse> makeRequest(
      GraphQlRequestContext context,
      org.hypertrace.gateway.service.v1.explore.ExploreRequest request) {
    return Single.fromFuture(
        this.grpcContextBuilder
            .build(context)
            .call(
                () ->
                    this.gatewayServiceStub
                        .withDeadlineAfter(
                            serviceConfig.getGatewayServiceTimeout().toMillis(), MILLISECONDS)
                        .explore(request)));
  }
}
