package org.hypertrace.graphql.entity.dao;

import com.google.common.collect.Streams;
import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GraphQlGrpcContextBuilder;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.gateway.service.GatewayServiceGrpc;
import org.hypertrace.gateway.service.v1.baseline.BaselineEntitiesRequest;
import org.hypertrace.gateway.service.v1.baseline.BaselineEntitiesResponse;
import org.hypertrace.gateway.service.v1.baseline.BaselineTimeAggregation;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.FunctionExpression;
import org.hypertrace.gateway.service.v1.common.Period;
import org.hypertrace.gateway.service.v1.common.TimeAggregation;
import org.hypertrace.gateway.service.v1.entity.EntitiesRequest;
import org.hypertrace.gateway.service.v1.entity.EntitiesResponse;
import org.hypertrace.gateway.service.v1.entity.Entity;
import org.hypertrace.graphql.entity.health.BaselineDao;
import org.hypertrace.graphql.entity.request.EntityRequest;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.request.MetricRequest;
import org.hypertrace.graphql.metric.request.MetricSeriesRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;

@Singleton
class GatewayBaselineDao implements BaselineDao {
  private static final int DEFAULT_DEADLINE_SEC = 10;
  private final GatewayServiceGrpc.GatewayServiceFutureStub gatewayServiceStub;
  private final GraphQlGrpcContextBuilder grpcContextBuilder;

  @Inject
  GatewayBaselineDao(
      GrpcChannelRegistry grpcChannelRegistry,
      GraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GraphQlGrpcContextBuilder grpcContextBuilder) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.gatewayServiceStub =
        GatewayServiceGrpc.newFutureStub(
                grpcChannelRegistry.forAddress(
                    serviceConfig.getGatewayServiceHost(), serviceConfig.getGatewayServicePort()))
            .withCallCredentials(credentials);
  }

  @Override
  public Single<BaselineEntitiesResponse> buildAndSubmitRequest(
      GraphQlRequestContext context,
      EntitiesRequest entitiesRequest,
      EntitiesResponse entitiesResponse,
      EntityRequest request) {
    return this.buildRequest(entitiesRequest, entitiesResponse, request)
        .flatMap(baselineEntitiesRequest -> makeRequest(context, baselineEntitiesRequest));
  }

  private Single<BaselineEntitiesRequest> buildRequest(
      EntitiesRequest entitiesRequest, EntitiesResponse entitiesResponse, EntityRequest request) {
    Set<String> baselineReqSet = getBaselineRequestSet(request);
    BaselineEntitiesRequest baselineEntitiesRequest =
        BaselineEntitiesRequest.newBuilder()
            .setStartTimeMillis(entitiesRequest.getStartTimeMillis())
            .setEndTimeMillis(entitiesRequest.getEndTimeMillis())
            .addAllEntityIds(
                entitiesResponse.getEntityList().stream()
                    .map(Entity::getId)
                    .collect(Collectors.toList()))
            .setEntityType(entitiesRequest.getEntityType())
            .addAllBaselineAggregateRequest(
                getFunctionExpressionList(entitiesRequest, baselineReqSet))
            .addAllBaselineMetricSeriesRequest(
                getBaselineTimeSeriesRequest(entitiesRequest, baselineReqSet))
            .build();
    return Single.just(baselineEntitiesRequest);
  }

  private Set<String> getBaselineRequestSet(EntityRequest request) {
    Stream<String> aggregationAliases = request.metricRequests().stream()
            .map(MetricRequest::aggregationRequests)
            .flatMap(Collection::stream)
            .filter(MetricAggregationRequest::baseline)
            .map(MetricAggregationRequest::alias);

    Stream<String> seriesAliases = request.metricRequests().stream()
            .map(MetricRequest::baselineSeriesRequests)
            .flatMap(Collection::stream)
            .map(MetricSeriesRequest::alias);

    return Streams.concat(aggregationAliases, seriesAliases)
            .collect(Collectors.toUnmodifiableSet());
  }

  private List<FunctionExpression> getFunctionExpressionList(
      EntitiesRequest entitiesRequest, Set<String> baselineSet) {
    List<Expression> selectionList = entitiesRequest.getSelectionList();
    List<FunctionExpression> functionExpressions = new ArrayList<>();
    for (Expression expression : selectionList) {
      if (expression.hasFunction() && baselineSet.contains(expression.getFunction().getAlias())) {
        functionExpressions.add(expression.getFunction());
      }
    }
    return functionExpressions;
  }

  private List<BaselineTimeAggregation> getBaselineTimeSeriesRequest(
      EntitiesRequest entitiesRequest, Set<String> requestMap) {
    List<TimeAggregation> timeSeriesList = entitiesRequest.getTimeAggregationList();
    List<BaselineTimeAggregation> baselineTimeAggregationList = new ArrayList<>();
    timeSeriesList.forEach(
        timeAggregation -> {
          if (timeAggregation.getAggregation().hasFunction()
              && requestMap.contains(timeAggregation.getAggregation().getFunction().getAlias())) {
            Period period = timeAggregation.getPeriod();
            FunctionExpression functionExpression = timeAggregation.getAggregation().getFunction();
            BaselineTimeAggregation baselineAgg =
                BaselineTimeAggregation.newBuilder()
                    .setAggregation(functionExpression)
                    .setPeriod(period)
                    .build();
            baselineTimeAggregationList.add(baselineAgg);
          }
        });
    return baselineTimeAggregationList;
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
            .callInContext(
                () ->
                    this.gatewayServiceStub
                        .withDeadlineAfter(DEFAULT_DEADLINE_SEC, SECONDS)
                        .getBaselineForEntities(request)));
  }
}
