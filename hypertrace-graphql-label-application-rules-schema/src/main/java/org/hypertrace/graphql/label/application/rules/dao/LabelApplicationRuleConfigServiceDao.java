package org.hypertrace.graphql.label.application.rules.dao;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Single;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;
import org.hypertrace.graphql.label.application.rules.request.LabelApplicationRuleCreateRequest;
import org.hypertrace.graphql.label.application.rules.request.LabelApplicationRuleDeleteRequest;
import org.hypertrace.graphql.label.application.rules.request.LabelApplicationRuleUpdateRequest;
import org.hypertrace.graphql.label.application.rules.schema.query.LabelApplicationRuleResultSet;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRule;
import org.hypertrace.label.application.rule.config.service.v1.GetLabelApplicationRulesRequest;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleConfigServiceGrpc;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleConfigServiceGrpc.LabelApplicationRuleConfigServiceFutureStub;

class LabelApplicationRuleConfigServiceDao implements LabelApplicationRuleDao {

  private final LabelApplicationRuleConfigServiceFutureStub
      labelApplicationRuleConfigServiceFutureStub;
  private final GrpcContextBuilder grpcContextBuilder;
  private final HypertraceGraphQlServiceConfig serviceConfig;
  private final LabelApplicationRuleRequestConverter requestConverter;
  private final LabelApplicationRuleResponseConverter responseConverter;

  @Inject
  LabelApplicationRuleConfigServiceDao(
      HypertraceGraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GrpcContextBuilder grpcContextBuilder,
      GrpcChannelRegistry grpcChannelRegistry,
      LabelApplicationRuleRequestConverter requestConverter,
      LabelApplicationRuleResponseConverter responseConverter) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.serviceConfig = serviceConfig;
    this.requestConverter = requestConverter;
    this.responseConverter = responseConverter;
    this.labelApplicationRuleConfigServiceFutureStub =
        LabelApplicationRuleConfigServiceGrpc.newFutureStub(
                grpcChannelRegistry.forAddress(
                    serviceConfig.getConfigServiceHost(), serviceConfig.getConfigServicePort()))
            .withCallCredentials(credentials);
  }

  @Override
  public Single<LabelApplicationRule> createLabelApplicationRule(
      LabelApplicationRuleCreateRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.labelApplicationRuleConfigServiceFutureStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(),
                                TimeUnit.MILLISECONDS)
                            .createLabelApplicationRule(
                                this.requestConverter.convertCreationRequest(request))))
        .flatMap(this.responseConverter::convertCreateLabelApplicationRuleResponse);
  }

  @Override
  public Single<LabelApplicationRuleResultSet> getLabelApplicationRules(ContextualRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.labelApplicationRuleConfigServiceFutureStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(),
                                TimeUnit.MILLISECONDS)
                            .getLabelApplicationRules(
                                GetLabelApplicationRulesRequest.getDefaultInstance())))
        .flatMap(this.responseConverter::convertGetLabelApplicationsRuleResponse);
  }

  @Override
  public Single<LabelApplicationRule> updateLabelApplicationRule(
      LabelApplicationRuleUpdateRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.labelApplicationRuleConfigServiceFutureStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(),
                                TimeUnit.MILLISECONDS)
                            .updateLabelApplicationRule(
                                this.requestConverter.convertUpdateRequest(request))))
        .flatMap(this.responseConverter::convertUpdateLabelApplicationRuleResponse);
  }

  @Override
  public Single<Boolean> deleteLabelApplicationRule(LabelApplicationRuleDeleteRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.labelApplicationRuleConfigServiceFutureStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(),
                                TimeUnit.MILLISECONDS)
                            .deleteLabelApplicationRule(
                                this.requestConverter.convertDeleteRequest(request))))
        .flatMap(
            unusedResponse -> this.responseConverter.buildDeleteLabelApplicationRuleResponse());
  }
}
