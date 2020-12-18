package org.hypertrace.graphql.entity.health;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GraphQlGrpcContextBuilder;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.gateway.service.GatewayServiceGrpc;
import org.hypertrace.gateway.service.v1.health.BaselineEntityRequest;
import org.hypertrace.gateway.service.v1.health.BaselineEntityResponse;
import org.hypertrace.graphql.entity.dao.GatewayServiceEntityRequestBuilder;
import org.hypertrace.graphql.entity.request.EntityRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import static java.util.concurrent.TimeUnit.SECONDS;

@Singleton
public class GatewayBaselineProviderImpl implements BaselineProvider {
  private static final int DEFAULT_DEADLINE_SEC = 10;
  private final GatewayServiceEntityRequestBuilder gatewayServiceEntityRequestBuilder;
  private GatewayServiceGrpc.GatewayServiceFutureStub gatewayServiceStub;
  private final GraphQlGrpcContextBuilder grpcContextBuilder;

  @Inject
  GatewayBaselineProviderImpl(
      GatewayServiceEntityRequestBuilder gatewayServiceEntityRequestBuilder,
      GrpcChannelRegistry grpcChannelRegistry,
      GraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GraphQlGrpcContextBuilder grpcContextBuilder) {
    this.gatewayServiceEntityRequestBuilder = gatewayServiceEntityRequestBuilder;
    this.grpcContextBuilder = grpcContextBuilder;
    this.gatewayServiceStub =
            GatewayServiceGrpc.newFutureStub(
                    grpcChannelRegistry.forAddress(
                            serviceConfig.getGatewayServiceHost(), serviceConfig.getGatewayServicePort()))
                    .withCallCredentials(credentials);
  }

  @Override
  public Single<BaselineEntityRequest> buildRequest(EntityRequest request) {
   // TODO implement this
    return Single.just(BaselineEntityRequest.newBuilder().build());
  }

  @Override
  public Single<BaselineEntityResponse> makeRequest(
      GraphQlRequestContext context, BaselineEntityRequest request) {
    return Single.fromFuture(
        this.grpcContextBuilder
            .build(context)
            .callInContext(
                () ->
                    this.gatewayServiceStub
                        .withDeadlineAfter(DEFAULT_DEADLINE_SEC, SECONDS)
                        .getBaselineForEntities(request)));
  }
}
