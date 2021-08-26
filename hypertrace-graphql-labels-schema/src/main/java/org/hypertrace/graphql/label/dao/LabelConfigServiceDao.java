package org.hypertrace.graphql.label.dao;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.graphql.label.schema.LabelResultSet;
import org.hypertrace.label.config.service.v1.GetLabelsRequest;
import org.hypertrace.label.config.service.v1.GetLabelsResponse;
import org.hypertrace.label.config.service.v1.LabelConfigServiceGrpc;
import org.hypertrace.label.config.service.v1.LabelConfigServiceGrpc.LabelConfigServiceFutureStub;

@Singleton
class LabelConfigServiceDao implements LabelDao {
  private final LabelConfigServiceFutureStub labelConfigServiceStub;
  private final GrpcContextBuilder grpcContextBuilder;
  private final GraphQlServiceConfig serviceConfig;
  private final LabelResponseConverter converter;

  @Inject
  LabelConfigServiceDao(
      GraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GrpcContextBuilder grpcContextBuilder,
      GrpcChannelRegistry grpcChannelRegistry,
      LabelResponseConverter converter) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.serviceConfig = serviceConfig;
    this.converter = converter;
    this.labelConfigServiceStub =
        LabelConfigServiceGrpc.newFutureStub(
                grpcChannelRegistry.forAddress(
                    serviceConfig.getConfigServiceHost(), serviceConfig.getConfigServicePort()))
            .withCallCredentials(credentials);
  }

  @Override
  public Single<LabelResultSet> getLabels(ContextualRequest request) {
    return this.makeRequest(request.context(), GetLabelsRequest.getDefaultInstance())
        .flatMap(serverResponse -> converter.convert(serverResponse));
  }

  private Single<GetLabelsResponse> makeRequest(
      GraphQlRequestContext context, GetLabelsRequest request) {
    return Single.fromFuture(
        this.grpcContextBuilder
            .build(context)
            .call(
                () ->
                    this.labelConfigServiceStub
                        .withDeadlineAfter(
                            serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                        .getLabels(request)));
  }
}
