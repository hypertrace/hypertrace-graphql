package org.hypertrace.graphql.entity.dao;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.rx.BoundedIoScheduler;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.gateway.service.GatewayServiceGrpc;
import org.hypertrace.gateway.service.GatewayServiceGrpc.GatewayServiceFutureStub;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.entity.EntitiesRequest;
import org.hypertrace.gateway.service.v1.entity.EntitiesResponse;
import org.hypertrace.gateway.service.v1.entity.Entity;
import org.hypertrace.graphql.entity.health.BaselineDao;
import org.hypertrace.graphql.entity.request.EntityLabelRequest;
import org.hypertrace.graphql.entity.request.EntityRequest;
import org.hypertrace.graphql.entity.schema.EntityResultSet;
import org.hypertrace.graphql.label.joiner.LabelJoiner;
import org.hypertrace.graphql.label.joiner.LabelJoinerBuilder;
import org.hypertrace.graphql.label.schema.LabelResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Singleton
class GatewayServiceEntityDao implements EntityDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(GatewayServiceEntityDao.class);
  private final GatewayServiceFutureStub gatewayServiceStub;
  private final GrpcContextBuilder grpcContextBuilder;
  private final GatewayServiceEntityRequestBuilder requestBuilder;
  private final GatewayServiceEntityConverter entityConverter;
  private final BaselineDao baselineDao;
  private final GraphQlServiceConfig serviceConfig;
  private final Scheduler boundedIoScheduler;
  private final LabelJoinerBuilder labelJoinerBuilder;

  @Inject
  GatewayServiceEntityDao(
      GraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GrpcContextBuilder grpcContextBuilder,
      GrpcChannelRegistry grpcChannelRegistry,
      GatewayServiceEntityRequestBuilder requestBuilder,
      GatewayServiceEntityConverter entityConverter,
      BaselineDao baselineDao,
      LabelJoinerBuilder labelJoinerBuilder,
      @BoundedIoScheduler Scheduler boundedIoScheduler) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.requestBuilder = requestBuilder;
    this.entityConverter = entityConverter;
    this.baselineDao = baselineDao;
    this.labelJoinerBuilder = labelJoinerBuilder;
    this.serviceConfig = serviceConfig;
    this.boundedIoScheduler = boundedIoScheduler;

    this.gatewayServiceStub =
        GatewayServiceGrpc.newFutureStub(
                grpcChannelRegistry.forAddress(
                    serviceConfig.getGatewayServiceHost(), serviceConfig.getGatewayServicePort()))
            .withCallCredentials(credentials);
  }

  @Override
  public Single<EntityResultSet> getEntities(EntityRequest request) {
    GraphQlRequestContext context = request.resultSetRequest().context();
    Single<EntitiesRequest> entitiesRequestSingle = this.requestBuilder.buildRequest(request);
    LOGGER.info(
        "get entities request from context is as follows {}", entitiesRequestSingle.blockingGet());
    return this.requestBuilder
        .buildRequest(request)
        .subscribeOn(this.boundedIoScheduler)
        .flatMap(serverRequest -> this.fetchAndMapEntities(context, request, serverRequest));
  }

  private Single<EntityResultSet> fetchAndMapEntities(
      GraphQlRequestContext context, EntityRequest request, EntitiesRequest serverRequest) {
    LOGGER.info("get entities request is as follows {}", serverRequest);
    return this.makeEntityRequest(context, serverRequest)
        .flatMap(serverResponse -> this.getEntityResultSet(request, serverRequest, serverResponse));
  }

  private Single<EntityResultSet> getEntityResultSet(
      EntityRequest request, EntitiesRequest serverRequest, EntitiesResponse serverResponse) {
    GraphQlRequestContext context = request.resultSetRequest().context();
    return Single.zip(
            baselineDao.getBaselines(context, serverRequest, serverResponse, request),
            buildLabelResultSetMap(context, request, serverResponse),
            (baselineResponse, labelResultSetMap) ->
                this.entityConverter.convert(
                    request, serverResponse, baselineResponse, labelResultSetMap))
        .flatMap(entityResultSet -> entityResultSet);
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

  private Single<Map<Entity, LabelResultSet>> buildLabelResultSetMap(
      GraphQlRequestContext context, EntityRequest request, EntitiesResponse entitiesResponse) {
    return request
        .labelRequest()
        .map(labelRequest -> labelJoinerBuilder.build(context))
        .orElse(Single.just(LabelJoiner.NO_OP_JOINER))
        .flatMap(
            joiner ->
                joiner.joinLabels(
                    entitiesResponse.getEntityList(),
                    entity -> Single.just(getLabelAttributeValue(request, entity))));
  }

  private List<String> getLabelAttributeValue(EntityRequest request, Entity entity) {
    return request
        .labelRequest()
        .map(EntityLabelRequest::labelIdArrayAttributeRequest)
        .map(AttributeRequest::asMapKey)
        .filter(entity::containsAttribute)
        .map(entity::getAttributeOrThrow)
        .<List<String>>map(Value::getStringArrayList)
        .orElseGet(
            () -> {
              log.warn("Unable to fetch labels attribute for entity with id {}", entity.getId());
              return Collections.emptyList();
            });
  }
}
