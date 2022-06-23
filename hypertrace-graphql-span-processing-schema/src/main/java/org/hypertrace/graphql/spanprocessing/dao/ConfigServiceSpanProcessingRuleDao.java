package org.hypertrace.graphql.spanprocessing.dao;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;
import org.hypertrace.graphql.spanprocessing.request.mutation.ApiNamingCreateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ApiNamingDeleteRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ApiNamingUpdateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanCreateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanDeleteRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanUpdateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.IncludeSpanCreateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.IncludeSpanDeleteRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.IncludeSpanUpdateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.SamplingConfigCreateRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.SamplingConfigDeleteRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.SamplingConfigUpdateRequest;
import org.hypertrace.graphql.spanprocessing.schema.mutation.DeleteSpanProcessingRuleResponse;
import org.hypertrace.graphql.spanprocessing.schema.query.ApiNamingRuleResultSet;
import org.hypertrace.graphql.spanprocessing.schema.query.ExcludeSpanRuleResultSet;
import org.hypertrace.graphql.spanprocessing.schema.query.IncludeSpanRuleResultSet;
import org.hypertrace.graphql.spanprocessing.schema.query.SamplingConfigsResultSet;
import org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRule;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRule;
import org.hypertrace.graphql.spanprocessing.schema.rule.IncludeSpanRule;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.SamplingConfig;
import org.hypertrace.span.processing.config.service.v1.GetAllApiNamingRulesRequest;
import org.hypertrace.span.processing.config.service.v1.GetAllExcludeSpanRulesRequest;
import org.hypertrace.span.processing.config.service.v1.GetAllIncludeSpanRulesRequest;
import org.hypertrace.span.processing.config.service.v1.GetAllSamplingConfigsRequest;
import org.hypertrace.span.processing.config.service.v1.SpanProcessingConfigServiceGrpc;

public class ConfigServiceSpanProcessingRuleDao implements SpanProcessingRuleDao {
  private final HypertraceGraphQlServiceConfig serviceConfig;
  private final GrpcContextBuilder grpcContextBuilder;
  private final ConfigServiceSpanProcessingResponseConverter responseConverter;
  private final ConfigServiceSpanProcessingRequestConverter requestConverter;
  private final SpanProcessingConfigServiceGrpc.SpanProcessingConfigServiceFutureStub configStub;

  @Inject
  ConfigServiceSpanProcessingRuleDao(
      HypertraceGraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GrpcContextBuilder grpcContextBuilder,
      GrpcChannelRegistry channelRegistry,
      ConfigServiceSpanProcessingResponseConverter responseConverter,
      ConfigServiceSpanProcessingRequestConverter requestConverter) {
    this.serviceConfig = serviceConfig;
    this.grpcContextBuilder = grpcContextBuilder;
    this.responseConverter = responseConverter;
    this.requestConverter = requestConverter;

    this.configStub =
        SpanProcessingConfigServiceGrpc.newFutureStub(
                channelRegistry.forAddress(
                    serviceConfig.getConfigServiceHost(), serviceConfig.getConfigServicePort()))
            .withCallCredentials(credentials);
  }

  @Override
  public Single<ExcludeSpanRuleResultSet> getExcludeSpanRules(ContextualRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .getAllExcludeSpanRules(
                                GetAllExcludeSpanRulesRequest.getDefaultInstance())))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<ExcludeSpanRule> createExcludeSpanRule(ExcludeSpanCreateRuleRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .createExcludeSpanRule(this.requestConverter.convert(request))))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<ExcludeSpanRule> updateExcludeSpanRule(ExcludeSpanUpdateRuleRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .updateExcludeSpanRule(this.requestConverter.convert(request))))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<DeleteSpanProcessingRuleResponse> deleteExcludeSpanRule(
      ExcludeSpanDeleteRuleRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .deleteExcludeSpanRule(this.requestConverter.convert(request))))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<IncludeSpanRuleResultSet> getIncludeSpanRules(ContextualRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .getAllIncludeSpanRules(
                                GetAllIncludeSpanRulesRequest.getDefaultInstance())))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<IncludeSpanRule> createIncludeSpanRule(IncludeSpanCreateRuleRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .createIncludeSpanRule(this.requestConverter.convert(request))))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<IncludeSpanRule> updateIncludeSpanRule(IncludeSpanUpdateRuleRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .updateIncludeSpanRule(this.requestConverter.convert(request))))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<DeleteSpanProcessingRuleResponse> deleteIncludeSpanRule(
      IncludeSpanDeleteRuleRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .deleteIncludeSpanRule(this.requestConverter.convert(request))))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<ApiNamingRuleResultSet> getApiNamingRules(ContextualRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .getAllApiNamingRules(
                                GetAllApiNamingRulesRequest.getDefaultInstance())))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<ApiNamingRule> createApiNamingRule(ApiNamingCreateRuleRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .createApiNamingRule(this.requestConverter.convert(request))))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<ApiNamingRule> updateApiNamingRule(ApiNamingUpdateRuleRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .updateApiNamingRule(this.requestConverter.convert(request))))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<DeleteSpanProcessingRuleResponse> deleteApiNamingRule(
      ApiNamingDeleteRuleRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .deleteApiNamingRule(this.requestConverter.convert(request))))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<SamplingConfigsResultSet> getSamplingConfigs(ContextualRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .getAllSamplingConfigs(
                                GetAllSamplingConfigsRequest.getDefaultInstance())))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<SamplingConfig> createSamplingConfig(SamplingConfigCreateRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .createSamplingConfig(this.requestConverter.convert(request))))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<SamplingConfig> updateSamplingConfig(SamplingConfigUpdateRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .updateSamplingConfig(this.requestConverter.convert(request))))
        .flatMap(this.responseConverter::convert);
  }

  @Override
  public Single<DeleteSpanProcessingRuleResponse> deleteSamplingConfig(
      SamplingConfigDeleteRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .call(
                    () ->
                        this.configStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(), MILLISECONDS)
                            .deleteSamplingConfig(this.requestConverter.convert(request))))
        .flatMap(this.responseConverter::convert);
  }
}
