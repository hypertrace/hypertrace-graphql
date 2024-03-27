package org.hypertrace.core.graphql.attributes;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hypertrace.core.attribute.service.cachingclient.CachingAttributeClient;
import org.hypertrace.core.attribute.service.v1.AttributeMetadata;
import org.hypertrace.core.attribute.service.v1.AttributeMetadataFilter;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.core.grpcutils.client.rx.GrpcRxExecutionContext;

@Singleton
class CachingAttributeStore implements AttributeStore {
  private final CachingAttributeClient cachingAttributeClient;
  private final IdLookup idLookup;
  private final GrpcContextBuilder grpcContextBuilder;
  private final AttributeModelTranslator translator;

  @Inject
  CachingAttributeStore(
      IdLookup idLookup,
      GrpcContextBuilder grpcContextBuilder,
      AttributeModelTranslator translator,
      GrpcChannelRegistry channelRegistry,
      GraphQlServiceConfig serviceConfig) {
    this(
        idLookup,
        grpcContextBuilder,
        translator,
        CachingAttributeClient.builder(
                channelRegistry.forAddress(
                    serviceConfig.getAttributeServiceHost(),
                    serviceConfig.getAttributeServicePort()))
            .withCacheExpiration(Duration.ofMinutes(3))
            .withMaximumCacheContexts(1000)
            .build());
  }

  CachingAttributeStore(
      IdLookup idLookup,
      GrpcContextBuilder grpcContextBuilder,
      AttributeModelTranslator translator,
      CachingAttributeClient cachingAttributeClient) {
    this.idLookup = idLookup;
    this.grpcContextBuilder = grpcContextBuilder;
    this.translator = translator;
    this.cachingAttributeClient = cachingAttributeClient;
  }

  @Override
  public Single<List<AttributeModel>> getAllExternal(GraphQlRequestContext requestContext) {
    return GrpcRxExecutionContext.forContext(this.grpcContextBuilder.build(requestContext))
        .wrapSingle(this.cachingAttributeClient::getAll)
        .flattenAsObservable(list -> list)
        .filter(attribute -> !attribute.getInternal())
        .mapOptional(this.translator::translate)
        .toList();
  }

  @Override
  public Single<AttributeModel> get(
      GraphQlRequestContext requestContext, String scope, String key) {
    return GrpcRxExecutionContext.forContext(this.grpcContextBuilder.build(requestContext))
        .wrapSingle(() -> this.cachingAttributeClient.get(scope, key))
        .toMaybe()
        .mapOptional(this.translator::translate)
        .switchIfEmpty(Single.error(this.buildErrorForMissingAttribute(scope, key)));
  }

  @Override
  public Single<AttributeModel> getIdAttribute(GraphQlRequestContext context, String scope) {
    return this.getIdKey(context, scope).flatMap(key -> this.get(context, scope, key));
  }

  @Override
  public Single<AttributeModel> getForeignIdAttribute(
      GraphQlRequestContext context, String scope, String foreignScope) {
    return this.getForeignIdKey(context, scope, foreignScope)
        .flatMap(key -> this.get(context, scope, key));
  }

  @Override
  public Single<AttributeModel> getAttributeById(
      GraphQlRequestContext context, String attributeId) {
    return grpcContextBuilder
        .build(context)
        .call(() -> cachingAttributeClient.get(attributeId))
        .mapOptional(this.translator::translate)
        .switchIfEmpty(Single.error(this.buildErrorForMissingAttributeId(attributeId)));
  }

  @Override
  public Completable create(
      final GraphQlRequestContext context, final List<AttributeMetadata> attributes) {
    return this.grpcContextBuilder
        .build(context)
        .call(() -> cachingAttributeClient.create(attributes));
  }

  @Override
  public Completable delete(
      final GraphQlRequestContext context, final AttributeIdentifier identifier) {
    return this.grpcContextBuilder
        .build(context)
        .call(() -> cachingAttributeClient.delete(buildFilter(identifier)));
  }

  @Override
  public Single<AttributeModel> update(
      final GraphQlRequestContext context,
      final AttributeIdentifier identifier,
      final AttributeUpdate update) {
    final Single<AttributeModel> metadataSingle =
        get(context, identifier.scope(), identifier.key());
    return metadataSingle.flatMap(
        metadata ->
            this.grpcContextBuilder
                .build(context)
                .call(() -> cachingAttributeClient.update(metadata.id(), update.buildUpdates()))
                .mapOptional(translator::translate)
                .switchIfEmpty(
                    Single.error(
                        this.buildErrorForMissingAttribute(identifier.scope(), identifier.key()))));
  }

  private Single<String> getForeignIdKey(
      GraphQlRequestContext context, String scope, String foreignScope) {
    return this.idLookup
        .foreignIdKey(context, scope, foreignScope)
        .switchIfEmpty(
            Single.error(this.buildErrorForMissingForeignScopeMapping(scope, foreignScope)));
  }

  private Single<String> getIdKey(GraphQlRequestContext context, String scope) {
    return this.idLookup
        .idKey(context, scope)
        .switchIfEmpty(Single.error(this.buildErrorForMissingIdMapping(scope)));
  }

  private NoSuchElementException buildErrorForMissingAttribute(String scope, String key) {
    return new NoSuchElementException(
        String.format("No attribute available for scope '%s' and key '%s'", scope, key));
  }

  private NoSuchElementException buildErrorForMissingAttributeId(String attributeId) {
    return new NoSuchElementException(
        String.format("No attribute available for attribute id '%s'", attributeId));
  }

  private NoSuchElementException buildErrorForMissingForeignScopeMapping(
      String scope, String foreignScope) {
    return new NoSuchElementException(
        String.format(
            "No id attribute registered for scope '%s' and foreign scope '%s'",
            scope, foreignScope));
  }

  private NoSuchElementException buildErrorForMissingIdMapping(String scope) {
    return new NoSuchElementException(
        String.format("No id attribute registered for scope '%s'", scope));
  }

  private AttributeMetadataFilter buildFilter(final AttributeIdentifier filter) {
    return AttributeMetadataFilter.newBuilder()
        .addKey(filter.key())
        .addScopeString(filter.scope())
        .build();
  }
}
