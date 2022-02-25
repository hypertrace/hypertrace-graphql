package org.hypertrace.graphql.spanprocessing.dao;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanCreateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanDeleteRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanUpdateRuleRequest;
import org.hypertrace.graphql.spanprocessing.schema.mutation.DeleteSpanProcessingRuleResponse;
import org.hypertrace.graphql.spanprocessing.schema.query.ExcludeSpanRuleResultSet;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRule;
import org.hypertrace.span.processing.config.service.v1.GetAllExcludeSpanRulesRequest;
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
  public Single<ExcludeSpanRuleResultSet> getRules(ContextualRequest request) {
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
  public Single<ExcludeSpanRule> createRule(ExcludeSpanCreateRuleRequest request) {
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
  public Single<ExcludeSpanRule> updateRule(ExcludeSpanUpdateRuleRequest request) {
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
  public Single<DeleteSpanProcessingRuleResponse> deleteRule(ExcludeSpanDeleteRuleRequest request) {
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
}
