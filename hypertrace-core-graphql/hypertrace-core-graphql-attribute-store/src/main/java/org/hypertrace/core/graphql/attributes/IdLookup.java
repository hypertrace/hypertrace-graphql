package org.hypertrace.core.graphql.attributes;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hypertrace.core.graphql.context.ContextualCachingKey;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.rx.BoundedIoScheduler;

@Singleton
class IdLookup {

  private final LoadingCache<ContextualCachingKey, Single<ImmutableTable<String, String, String>>>
      idMapCache;
  private final Set<IdMappingLoader> mappingLoaders;
  private final Scheduler boundedIoScheduler;

  @Inject
  IdLookup(
      Set<IdMapping> idMappings,
      Set<IdMappingLoader> mappingLoaders,
      @BoundedIoScheduler Scheduler boundedIoScheduler) {
    this.mappingLoaders =
        ImmutableSet.<IdMappingLoader>builder()
            .addAll(mappingLoaders)
            .add(requestContext -> Observable.fromIterable(idMappings))
            .build();
    this.boundedIoScheduler = boundedIoScheduler;
    this.idMapCache =
        CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build(CacheLoader.from(this::loadMappingsForContext));
  }

  Maybe<String> idKey(GraphQlRequestContext requestContext, String scope) {
    return this.foreignIdKey(requestContext, scope, scope);
  }

  Maybe<String> foreignIdKey(
      GraphQlRequestContext requestContext, String scope, String foreignScope) {
    return this.getOrInvalidate(requestContext)
        .mapOptional(table -> Optional.ofNullable(table.get(scope, foreignScope)));
  }

  private Single<ImmutableTable<String, String, String>> loadMappingsForContext(
      ContextualCachingKey key) {
    return Observable.fromIterable(this.mappingLoaders)
        .flatMap(
            idMappingLoader ->
                idMappingLoader.loadMappings(key.getContext()).subscribeOn(this.boundedIoScheduler))
        // This is added to have distinct values while building the
        // immutable table in case of having duplicate id definitions
        .distinct()
        .collect(
            ImmutableTable.toImmutableTable(
                IdMapping::containingScope, IdMapping::foreignScope, IdMapping::idAttribute))
        .cache();
  }

  private Single<ImmutableTable<String, String, String>> getOrInvalidate(
      GraphQlRequestContext context) {
    return this.idMapCache
        .getUnchecked(context.getCachingKey())
        .doOnError(x -> this.idMapCache.invalidate(context.getCachingKey()));
  }
}
