package org.hypertrace.graphql.spaces.dao;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Single;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.utils.grpc.GraphQlGrpcContextBuilder;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.graphql.config.HypertraceGraphQlServiceConfig;
import org.hypertrace.graphql.spaces.request.SpaceConfigRuleCreationRequest;
import org.hypertrace.graphql.spaces.request.SpaceConfigRuleDeleteRequest;
import org.hypertrace.graphql.spaces.request.SpaceConfigRuleUpdateRequest;
import org.hypertrace.graphql.spaces.schema.query.SpaceConfigRuleResultSet;
import org.hypertrace.graphql.spaces.schema.shared.SpaceConfigRule;
import org.hypertrace.spaces.config.service.v1.SpacesConfigServiceGrpc;
import org.hypertrace.spaces.config.service.v1.SpacesConfigServiceGrpc.SpacesConfigServiceFutureStub;

class ConfigServiceSpacesConfigDao implements SpacesConfigDao {

  private final SpacesConfigServiceFutureStub spaceConfigServiceStub;
  private final GraphQlGrpcContextBuilder grpcContextBuilder;
  private final SpaceConfigRulesRequestConverter requestConverter;
  private final SpaceConfigRulesResponseConverter responseConverter;
  private final HypertraceGraphQlServiceConfig serviceConfig;

  @Inject
  ConfigServiceSpacesConfigDao(
      HypertraceGraphQlServiceConfig serviceConfig,
      CallCredentials credentials,
      GraphQlGrpcContextBuilder grpcContextBuilder,
      GrpcChannelRegistry grpcChannelRegistry,
      SpaceConfigRulesRequestConverter requestConverter,
      SpaceConfigRulesResponseConverter responseConverter) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.requestConverter = requestConverter;
    this.responseConverter = responseConverter;
    this.serviceConfig = serviceConfig;

    this.spaceConfigServiceStub =
        SpacesConfigServiceGrpc.newFutureStub(
                grpcChannelRegistry.forAddress(
                    serviceConfig.getConfigServiceHost(), serviceConfig.getConfigServicePort()))
            .withCallCredentials(credentials);
  }

  @Override
  public Single<SpaceConfigRuleResultSet> getAllRules(GraphQlRequestContext requestContext) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(requestContext)
                .callInContext(
                    () ->
                        this.spaceConfigServiceStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(),
                                TimeUnit.MILLISECONDS)
                            .getRules(this.requestConverter.convertGetRequest())))
        .flatMap(this.responseConverter::convertGetResponse);
  }

  @Override
  public Single<SpaceConfigRule> createRule(SpaceConfigRuleCreationRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .callInContext(
                    () ->
                        this.spaceConfigServiceStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(),
                                TimeUnit.MILLISECONDS)
                            .createRule(this.requestConverter.convertCreationRequest(request))))
        .flatMap(this.responseConverter::convertRule);
  }

  @Override
  public Single<SpaceConfigRule> updateRule(SpaceConfigRuleUpdateRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .callInContext(
                    () ->
                        this.spaceConfigServiceStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(),
                                TimeUnit.MILLISECONDS)
                            .updateRule(this.requestConverter.convertUpdateRequest(request))))
        .flatMap(this.responseConverter::convertRule);
  }

  @Override
  public Single<Boolean> deleteRule(SpaceConfigRuleDeleteRequest request) {
    return Single.fromFuture(
            this.grpcContextBuilder
                .build(request.context())
                .callInContext(
                    () ->
                        this.spaceConfigServiceStub
                            .withDeadlineAfter(
                                serviceConfig.getConfigServiceTimeout().toMillis(),
                                TimeUnit.MILLISECONDS)
                            .deleteRule(this.requestConverter.convertDeleteRequest(request))))
        .flatMap(unusedResponse -> this.responseConverter.buildDeleteResponse());
  }
}
