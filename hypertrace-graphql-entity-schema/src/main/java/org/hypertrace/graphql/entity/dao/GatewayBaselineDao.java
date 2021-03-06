package org.hypertrace.graphql.entity.dao;

import static io.reactivex.rxjava3.core.Single.zip;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hypertrace.core.graphql.common.utils.CollectorUtils.flatten;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.rx.BoundedIoScheduler;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.gateway.service.GatewayServiceGrpc;
import org.hypertrace.gateway.service.v1.baseline.BaselineEntitiesRequest;
import org.hypertrace.gateway.service.v1.baseline.BaselineEntitiesResponse;
import org.hypertrace.gateway.service.v1.baseline.BaselineTimeAggregation;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.FunctionExpression;
import org.hypertrace.gateway.service.v1.common.TimeAggregation;
import org.hypertrace.gateway.service.v1.entity.EntitiesRequest;
import org.hypertrace.gateway.service.v1.entity.EntitiesResponse;
import org.hypertrace.gateway.service.v1.entity.Entity;
import org.hypertrace.graphql.entity.health.BaselineDao;
import org.hypertrace.graphql.entity.request.EntityRequest;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.request.MetricRequest;
import org.hypertrace.graphql.metric.request.MetricSeriesRequest;

@Singleton
class GatewayBaselineDao implements BaselineDao {
  private final GatewayServiceGrpc.GatewayServiceFutureStub gatewayServiceStub;
  private final GrpcContextBuilder grpcContextBuilder;
  private final Scheduler boundedIoScheduler;
  private final Converter<Collection<MetricAggregationRequest>, Set<Expression>>
      aggregationConverter;
  private final Converter<Collection<MetricSeriesRequest>, Set<TimeAggregation>> seriesConverter;
  private final GraphQlServiceConfig serviceConfig;

  @Inject
  GatewayBaselineDao(
      GrpcChannelRegistry grpcChannelRegistry,
      GraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GrpcContextBuilder grpcContextBuilder,
      @BoundedIoScheduler Scheduler boundedIoScheduler,
      Converter<Collection<MetricAggregationRequest>, Set<Expression>> aggregationConverter,
      Converter<Collection<MetricSeriesRequest>, Set<TimeAggregation>> seriesConverter) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.boundedIoScheduler = boundedIoScheduler;
    this.gatewayServiceStub =
        GatewayServiceGrpc.newFutureStub(
                grpcChannelRegistry.forAddress(
                    serviceConfig.getGatewayServiceHost(), serviceConfig.getGatewayServicePort()))
            .withCallCredentials(credentials);
    this.seriesConverter = seriesConverter;
    this.aggregationConverter = aggregationConverter;
    this.serviceConfig = serviceConfig;
  }

  @Override
  public Single<BaselineEntitiesResponse> getBaselines(
      GraphQlRequestContext context,
      EntitiesRequest entitiesRequest,
      EntitiesResponse entitiesResponse,
      EntityRequest request) {
    return this.buildRequest(entitiesRequest, entitiesResponse, request)
        .subscribeOn(this.boundedIoScheduler)
        .flatMap(baselineEntitiesRequest -> makeRequest(context, baselineEntitiesRequest));
  }

  private Single<BaselineEntitiesRequest> buildRequest(
      EntitiesRequest entitiesRequest,
      EntitiesResponse entitiesResponse,
      EntityRequest entityRequest) {
    return zip(
        this.aggregationConverter.convert(
            entityRequest.metricRequests().stream()
                .map(MetricRequest::aggregationRequests)
                .flatMap(Collection::stream)
                .filter(MetricAggregationRequest::baseline)
                .collect(Collectors.toUnmodifiableList())),
        this.seriesConverter.convert(
            entityRequest.metricRequests().stream()
                .collect(flatten(MetricRequest::baselineSeriesRequests))),
        (aggregations, seriesRequests) ->
            BaselineEntitiesRequest.newBuilder()
                .setStartTimeMillis(entitiesRequest.getStartTimeMillis())
                .setEndTimeMillis(entitiesRequest.getEndTimeMillis())
                .addAllEntityIds(
                    entitiesResponse.getEntityList().stream()
                        .map(Entity::getId)
                        .collect(Collectors.toList()))
                .setEntityType(entitiesRequest.getEntityType())
                .addAllBaselineAggregateRequest(getBaselineAggregateRequests(aggregations))
                .addAllBaselineMetricSeriesRequest(getBaselineTimeSeriesRequests(seriesRequests))
                .build());
  }

  private Set<BaselineTimeAggregation> getBaselineTimeSeriesRequests(
      Set<TimeAggregation> seriesRequests) {
    return seriesRequests.stream()
        .map(
            seriesRequest ->
                BaselineTimeAggregation.newBuilder()
                    .setAggregation(seriesRequest.getAggregation().getFunction())
                    .setPeriod(seriesRequest.getPeriod())
                    .build())
        .collect(Collectors.toUnmodifiableSet());
  }

  private Set<FunctionExpression> getBaselineAggregateRequests(Set<Expression> aggregations) {
    return aggregations.stream()
        .map(Expression::getFunction)
        .collect(Collectors.toUnmodifiableSet());
  }

  private Single<BaselineEntitiesResponse> makeRequest(
      GraphQlRequestContext context, BaselineEntitiesRequest request) {
    if (request.getBaselineAggregateRequestCount() == 0
        && request.getBaselineMetricSeriesRequestCount() == 0) {
      return Single.just(BaselineEntitiesResponse.getDefaultInstance());
    }
    return Single.fromFuture(
        this.grpcContextBuilder
            .build(context)
            .call(
                () ->
                    this.gatewayServiceStub
                        .withDeadlineAfter(
                            serviceConfig.getGatewayServiceTimeout().toMillis(), MILLISECONDS)
                        .getBaselineForEntities(request)));
  }
}
