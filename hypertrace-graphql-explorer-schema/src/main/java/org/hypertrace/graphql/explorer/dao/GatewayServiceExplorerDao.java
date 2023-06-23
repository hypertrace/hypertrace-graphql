package org.hypertrace.graphql.explorer.dao;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.request.transformation.RequestTransformer;
import org.hypertrace.core.graphql.rx.BoundedIoScheduler;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.gateway.service.GatewayServiceGrpc;
import org.hypertrace.gateway.service.GatewayServiceGrpc.GatewayServiceFutureStub;
import org.hypertrace.gateway.service.v1.explore.ExploreResponse;
import org.hypertrace.graphql.explorer.fetcher.ExploreResultMapKey;
import org.hypertrace.graphql.explorer.request.ExploreRequest;
import org.hypertrace.graphql.explorer.schema.ExploreResult;
import org.hypertrace.graphql.explorer.schema.ExploreResultSet;
import org.hypertrace.graphql.explorer.schema.Selection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        .flatMap(
            serverResponse ->
                this.responseConverter
                    .convert(request, serverResponse)
                    .map(resultSet -> updateName(request, resultSet)));
  }

  private ExploreResultSet updateName(ExploreRequest request, ExploreResultSet resultSet) {
    if (request.scope().equals("DOMAIN_EVENT") && request.groupByAttributeRequests().stream()
        .anyMatch(attr -> attr.attributeExpressionAssociation().value().key().equals("name"))) {

      List<String> eventNames =
          resultSet.results().stream()
              .map(
                  result ->
                      result.selectionMap().keySet().stream()
                          .filter(key -> key.getAttributeExpression().key().equals("name"))
                          .findFirst()
                          .map(key -> result.selectionMap().get(key).value().toString()))
              .filter(Optional::isPresent)
              .map(Optional::get)
              .collect(Collectors.toList());

      // TODO: here need to plugin call to python server to get summaries
      Map<String, String> descriptions =
          eventNames.stream()
              .collect(Collectors.toMap(Function.identity(), str -> str + " :: some summary " + str.length()));
      //          Map.of(
      //              "MSSQL code execution and information gathering attempts",
      //              "Some Description MYSQL",
      //              "HTTP Response Splitting Attack (130)",
      //              "some description HTTP");

      for (ExploreResult result : resultSet.results()) {
        Map<ExploreResultMapKey, Selection> selectionMap = result.selectionMap();
        selectionMap.forEach(
            (key, value) -> {
              if (key.getAttributeExpression().key().equals("name")
                  && descriptions.containsKey(value.value().toString())) {
                selectionMap.put(
                    key,
                    new GatewayServiceSelectionMapConverter.ConvertedSelection(
                        value.type(),
                        value.value().toString()
                            + " :: "
                            + descriptions.get(value.value().toString())));
              }
            });
      }
    }
    return resultSet;
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
