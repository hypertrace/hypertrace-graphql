package org.hypertrace.graphql.entity.health;

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
import org.hypertrace.graphql.entity.dao.GatewayServiceEntityRequestBuilder;
import org.hypertrace.graphql.entity.request.EntityRequest;
import org.hypertrace.graphql.metric.request.Baseline;
import org.hypertrace.graphql.metric.request.MetricRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  public Single<BaselineEntitiesRequest> buildRequest(
      EntitiesRequest entitiesRequest, EntitiesResponse entitiesResponse, EntityRequest request) {
    Map<String, Baseline> baselineRequestMap = getBaselineRequestMap(request);
    BaselineEntitiesRequest baselineEntitiesRequest =
        BaselineEntitiesRequest.newBuilder()
            .setStartTimeMillis(entitiesRequest.getStartTimeMillis())
            .setEndTimeMillis(entitiesRequest.getEndTimeMillis())
            .addAllEntityIds(
                entitiesResponse.getEntityList().stream()
                    .map(x -> x.getId())
                    .collect(Collectors.toList()))
            .setEntityType(entitiesRequest.getEntityType())
            .addAllBaselineAggregateRequest(
                getFunctionExpression(entitiesRequest, baselineRequestMap))
            .addAllBaselineMetricSeriesRequest(
                getBaselineTimeSeriesRequest(entitiesRequest, baselineRequestMap))
            .build();
    return Single.just(baselineEntitiesRequest);
  }

  private Map<String, Baseline> getBaselineRequestMap(EntityRequest request) {
    Map<String, Baseline> baselineMap = new HashMap<>();
    List<MetricRequest> metricRequests = request.metricRequests();
    if (metricRequests.size() > 0) {
      for (MetricRequest metricRequest : metricRequests) {
        baselineMap.putAll(
            metricRequest.aggregationRequests().stream()
                .filter(x -> x.baseline() != null)
                .collect(Collectors.toMap(x -> x.alias(), y -> y.baseline())));
        baselineMap.putAll(
            metricRequest.seriesRequests().stream()
                .filter(x -> x.aggregationRequest().baseline() != null)
                .collect(Collectors.toMap(x -> x.alias(), y -> y.aggregationRequest().baseline())));
      }
    }
    return baselineMap;
  }

  private List<FunctionExpression> getFunctionExpression(
      EntitiesRequest entitiesRequest, Map<String, Baseline> requestMap) {
    List<Expression> selectionlist = entitiesRequest.getSelectionList();
    List<FunctionExpression> functionExpressions = new ArrayList<>();
    for (Expression expression : selectionlist) {
      if (expression.hasFunction() && requestMap.containsKey(expression.getFunction().getAlias())) {
        functionExpressions.add(expression.getFunction());
      }
    }
    return functionExpressions;
  }

  private List<BaselineTimeAggregation> getBaselineTimeSeriesRequest(
      EntitiesRequest entitiesRequest, Map<String, Baseline> requestMap) {
    List<TimeAggregation> timeSeriesList = entitiesRequest.getTimeAggregationList();
    List<BaselineTimeAggregation> baselineTimeAggregationList = new ArrayList<>();
    timeSeriesList.forEach(
        timeAggregation -> {
          if (timeAggregation.getAggregation().hasFunction()
              && requestMap.containsKey(
                  timeAggregation.getAggregation().getFunction().getAlias())) {
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

  @Override
  public Single<BaselineEntitiesResponse> makeRequest(
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
