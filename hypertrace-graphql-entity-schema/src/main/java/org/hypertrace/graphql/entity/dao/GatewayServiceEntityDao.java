package org.hypertrace.graphql.entity.dao;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.rx.BoundedIoScheduler;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.gateway.service.GatewayServiceGrpc;
import org.hypertrace.gateway.service.GatewayServiceGrpc.GatewayServiceFutureStub;
import org.hypertrace.gateway.service.v1.entity.EntitiesRequest;
import org.hypertrace.gateway.service.v1.entity.EntitiesResponse;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;
import org.hypertrace.graphql.entity.health.BaselineDao;
import org.hypertrace.graphql.entity.request.EntityRequest;
import org.hypertrace.graphql.entity.schema.EntityResultSet;
import org.hypertrace.label.config.service.v1.GetLabelsRequest;
import org.hypertrace.label.config.service.v1.GetLabelsResponse;
import org.hypertrace.label.config.service.v1.LabelsConfigServiceGrpc;
import org.hypertrace.label.config.service.v1.LabelsConfigServiceGrpc.LabelsConfigServiceFutureStub;

@Singleton
class GatewayServiceEntityDao implements EntityDao {
  private final GatewayServiceFutureStub gatewayServiceStub;
  private final GrpcContextBuilder grpcContextBuilder;
  private final GatewayServiceEntityRequestBuilder requestBuilder;
  private final GatewayServiceEntityConverter entityConverter;
  private final BaselineDao baselineDao;
  private final HypertraceGraphQlServiceConfig serviceConfig;
  private final Scheduler boundedIoScheduler;
  private final LabelsConfigServiceFutureStub labelConfigServiceStub;

  @Inject
  GatewayServiceEntityDao(
      HypertraceGraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GrpcContextBuilder grpcContextBuilder,
      GrpcChannelRegistry grpcChannelRegistry,
      GatewayServiceEntityRequestBuilder requestBuilder,
      GatewayServiceEntityConverter entityConverter,
      BaselineDao baselineDao,
      @BoundedIoScheduler Scheduler boundedIoScheduler) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.requestBuilder = requestBuilder;
    this.entityConverter = entityConverter;
    this.baselineDao = baselineDao;
    this.serviceConfig = serviceConfig;
    this.boundedIoScheduler = boundedIoScheduler;

    this.gatewayServiceStub =
        GatewayServiceGrpc.newFutureStub(
                grpcChannelRegistry.forAddress(
                    serviceConfig.getGatewayServiceHost(), serviceConfig.getGatewayServicePort()))
            .withCallCredentials(credentials);

    this.labelConfigServiceStub =
        LabelsConfigServiceGrpc.newFutureStub(
                grpcChannelRegistry.forAddress(
                    serviceConfig.getConfigServiceHost(), serviceConfig.getConfigServicePort()))
            .withCallCredentials(credentials);
  }

  @Override
  public Single<EntityResultSet> getEntities(EntityRequest request) {
    GraphQlRequestContext context = request.resultSetRequest().context();
    Single<EntitiesRequest> entitiesRequestSingle = this.requestBuilder.buildRequest(request);
    return Single.zip(
            entitiesRequestSingle,
            entitiesRequestSingle
                .subscribeOn(this.boundedIoScheduler)
                .flatMap(serverRequest -> this.makeEntityRequest(context, serverRequest)),
            this.makeLabelRequest(context, request).subscribeOn(this.boundedIoScheduler),
            (entitiesRequest, entitiesResponse, labelsResponse) -> {
              return getEntityResultSet(request, entitiesRequest, entitiesResponse, labelsResponse);
            })
        .flatMap(entityResultSet -> entityResultSet);
  }

  private Single<EntityResultSet> getEntityResultSet(
      EntityRequest request,
      EntitiesRequest entitiesRequest,
      EntitiesResponse entitiesResponse,
      Optional<GetLabelsResponse> labelsResponse) {
    GraphQlRequestContext context = request.resultSetRequest().context();
    return baselineDao
        .getBaselines(context, entitiesRequest, entitiesResponse, request)
        .flatMap(
            baselineResponse ->
                this.entityConverter.convert(
                    request, entitiesResponse, baselineResponse, labelsResponse));
  }

  private Single<EntitiesResponse> makeEntityRequest(
      GraphQlRequestContext context, EntitiesRequest request) {
    return Single.fromFuture(
        this.grpcContextBuilder
            .build(context)
            .call(
                () ->
                    this.gatewayServiceStub
                        .withDeadlineAfter(
                            serviceConfig.getGatewayServiceTimeout().toMillis(), MILLISECONDS)
                        .getEntities(request)));
  }

  private Single<Optional<GetLabelsResponse>> makeLabelRequest(
      GraphQlRequestContext context, EntityRequest request) {
    return request
        .labelRequest()
        .map(
            labelRequest ->
                Single.fromFuture(
                        this.grpcContextBuilder
                            .build(context)
                            .call(
                                () ->
                                    this.labelConfigServiceStub
                                        .withDeadlineAfter(
                                            serviceConfig.getConfigServiceTimeout().toMillis(),
                                            MILLISECONDS)
                                        .getLabels(GetLabelsRequest.getDefaultInstance())))
                    .map(Optional::of))
        .orElse(Single.just(Optional.empty()));
  }
}
