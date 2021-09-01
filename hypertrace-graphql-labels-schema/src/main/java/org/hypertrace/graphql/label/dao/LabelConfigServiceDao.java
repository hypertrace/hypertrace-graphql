package org.hypertrace.graphql.label.dao;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Single;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;
import org.hypertrace.graphql.label.request.LabelCreateRequest;
import org.hypertrace.graphql.label.request.LabelDeleteRequest;
import org.hypertrace.graphql.label.request.LabelUpdateRequest;
import org.hypertrace.graphql.label.schema.Label;
import org.hypertrace.graphql.label.schema.LabelResultSet;
import org.hypertrace.label.config.service.v1.GetLabelsRequest;
import org.hypertrace.label.config.service.v1.GetLabelsResponse;
import org.hypertrace.label.config.service.v1.LabelsConfigServiceGrpc;

class LabelConfigServiceDao implements LabelDao {
  private final LabelsConfigServiceGrpc.LabelsConfigServiceFutureStub labelConfigServiceStub;
  private final GrpcContextBuilder grpcContextBuilder;
  private final HypertraceGraphQlServiceConfig serviceConfig;
  private final LabelRequestConverter requestConverter;
  private final LabelResponseConverter responseConverter;

  @Inject
  LabelConfigServiceDao(
      HypertraceGraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GrpcContextBuilder grpcContextBuilder,
      GrpcChannelRegistry grpcChannelRegistry,
      LabelRequestConverter requestConverter,
      LabelResponseConverter responseConverter) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.serviceConfig = serviceConfig;
    this.requestConverter = requestConverter;
    this.responseConverter = responseConverter;
    this.labelConfigServiceStub =
        LabelsConfigServiceGrpc.newFutureStub(
                grpcChannelRegistry.forAddress(
                    serviceConfig.getConfigServiceHost(), serviceConfig.getConfigServicePort()))
            .withCallCredentials(credentials);
  }

  @Override
  public Single<LabelResultSet> getLabels(ContextualRequest request) {
    return this.makeRequest(request.context(), GetLabelsRequest.getDefaultInstance())
        .flatMap(serverResponse -> responseConverter.convert(serverResponse));
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

  @Override
  public Single<Label> createLabel(LabelCreateRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.labelConfigServiceStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(),
                                TimeUnit.MILLISECONDS)
                            .createLabel(this.requestConverter.convertCreationRequest(request))))
        .flatMap(this.responseConverter::convertLabel);
  }

  @Override
  public Single<Boolean> deleteLabel(LabelDeleteRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.labelConfigServiceStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(),
                                TimeUnit.MILLISECONDS)
                            .deleteLabel(this.requestConverter.convertDeleteRequest(request))))
        .flatMap(unusedResponse -> this.responseConverter.buildDeleteResponse());
  }

  @Override
  public Single<Label> updateLabel(LabelUpdateRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.labelConfigServiceStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(),
                                TimeUnit.MILLISECONDS)
                            .updateLabel(this.requestConverter.convertUpdateRequest(request))))
        .flatMap(this.responseConverter::convertUpdateLabel);
  }
}
