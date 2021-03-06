package org.hypertrace.graphql.entity.joiner;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Tables.immutableCell;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.Tables;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.FilterRequestBuilder;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequestBuilder;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterOperatorType;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterType;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.rx.BoundedIoScheduler;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;
import org.hypertrace.graphql.entity.dao.EntityDao;
import org.hypertrace.graphql.entity.request.EdgeSetGroupRequest;
import org.hypertrace.graphql.entity.request.EntityRequest;
import org.hypertrace.graphql.entity.schema.Entity;
import org.hypertrace.graphql.entity.schema.EntityJoinable;
import org.hypertrace.graphql.entity.schema.EntityResultSet;
import org.hypertrace.graphql.entity.schema.argument.EntityTypeStringArgument;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.request.MetricRequest;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;

class DefaultEntityJoinerBuilder implements EntityJoinerBuilder {

  private static final int ZERO_OFFSET = 0;

  private final EntityDao entityDao;
  private final GraphQlSelectionFinder selectionFinder;
  private final ArgumentDeserializer argumentDeserializer;
  private final ResultSetRequestBuilder resultSetRequestBuilder;
  private final FilterRequestBuilder filterRequestBuilder;
  private final Scheduler boundedIoScheduler;

  @Inject
  DefaultEntityJoinerBuilder(
      EntityDao entityDao,
      GraphQlSelectionFinder selectionFinder,
      ArgumentDeserializer argumentDeserializer,
      ResultSetRequestBuilder resultSetRequestBuilder,
      FilterRequestBuilder filterRequestBuilder,
      @BoundedIoScheduler Scheduler boundedIoScheduler) {

    this.entityDao = entityDao;
    this.selectionFinder = selectionFinder;
    this.argumentDeserializer = argumentDeserializer;
    this.resultSetRequestBuilder = resultSetRequestBuilder;
    this.filterRequestBuilder = filterRequestBuilder;
    this.boundedIoScheduler = boundedIoScheduler;
  }

  @Override
  public Single<EntityJoiner> build(
      GraphQlRequestContext context,
      DataFetchingFieldSelectionSet selectionSet,
      List<String> pathToEntityJoinable) {
    return Single.just(
        new DefaultEntityJoiner(
            context, this.groupEntityFieldsByType(selectionSet, pathToEntityJoinable)));
  }

  private String getEntityType(SelectedField entityField) {
    return argumentDeserializer
        .deserializePrimitive(entityField.getArguments(), EntityTypeStringArgument.class)
        .orElseThrow();
  }

  private Multimap<String, SelectedField> groupEntityFieldsByType(
      DataFetchingFieldSelectionSet selectionSet, List<String> pathToEntityJoinable) {

    List<String> fullPath =
        copyOf(concat(pathToEntityJoinable, List.of(EntityJoinable.ENTITY_KEY)));
    return selectionFinder
        .findSelections(selectionSet, SelectionQuery.builder().selectionPath(fullPath).build())
        .map(field -> Map.entry(this.getEntityType(field), field))
        .collect(this.setMultiMapCollector());
  }

  private <R, C, V> Collector<Cell<R, C, V>, ?, ImmutableTable<R, C, V>> tableCollector() {
    return ImmutableTable.toImmutableTable(Cell::getRowKey, Cell::getColumnKey, Cell::getValue);
  }

  private <K, V> Collector<Entry<K, V>, ?, ImmutableSetMultimap<K, V>> setMultiMapCollector() {
    return ImmutableSetMultimap.toImmutableSetMultimap(Entry::getKey, Entry::getValue);
  }

  @AllArgsConstructor
  private class DefaultEntityJoiner implements EntityJoiner {
    private final GraphQlRequestContext context;
    private final Multimap<String, SelectedField> entityFieldsByType;

    @Override
    public <T> Single<Table<T, String, Entity>> joinEntities(
        Collection<T> joinSources, EntityIdGetter<T> idGetter) {
      return this.buildRequestedIdTable(joinSources, idGetter).flatMap(this::joinEntities);
    }

    public <T> Single<Table<T, String, Entity>> joinEntities(
        Table<T, String, String> requestedEntityIdTable) {
      return this.buildEntityRequests(context, requestedEntityIdTable)
          .flatMap(this::fetchEntityData)
          .map(this::buildEntityResultTable)
          .map(resultTable -> this.mapResponses(requestedEntityIdTable, resultTable));
    }

    private Single<Set<EntityResultSet>> fetchEntityData(Collection<EntityRequest> entityRequests) {
      return Observable.fromIterable(entityRequests)
          .flatMapSingle(request -> entityDao.getEntities(request).subscribeOn(boundedIoScheduler))
          .collect(Collectors.toUnmodifiableSet());
    }

    private <T> Table<T, String, Entity> mapResponses(
        Table<T, String, String> requestedEntityIdTable,
        Table<String, String, Entity> entityResultTable) {

      return requestedEntityIdTable.rowKeySet().stream()
          .flatMap(
              source ->
                  this.buildResultCellsForSource(
                      source, requestedEntityIdTable.row(source), entityResultTable)
                      .stream())
          .collect(tableCollector());
    }

    private <T> Collection<Cell<T, String, Entity>> buildResultCellsForSource(
        T joinSource,
        Map<String, String> requestedIdsByType,
        Table<String, String, Entity> entityResultTable) {
      return requestedIdsByType.entrySet().stream()
          .map(
              typeIdPair ->
                  immutableCell(
                      joinSource,
                      typeIdPair.getKey(),
                      entityResultTable.get(typeIdPair.getKey(), typeIdPair.getValue())))
          .collect(Collectors.toUnmodifiableSet());
    }

    private <T> Single<ImmutableTable<T, String, String>> buildRequestedIdTable(
        Collection<T> joinSources, EntityIdGetter<T> idGetter) {
      return Observable.fromIterable(joinSources)
          .flatMap(source -> this.buildRequestedIdsForSource(source, idGetter))
          .collect(tableCollector());
    }

    private <T> Observable<Cell<T, String, String>> buildRequestedIdsForSource(
        T source, EntityIdGetter<T> idGetter) {
      return Observable.fromIterable(entityFieldsByType.keySet())
          .flatMapMaybe(
              entityType -> this.maybeBuildCellForSourceAndType(source, entityType, idGetter));
    }

    private <T> Maybe<Cell<T, String, String>> maybeBuildCellForSourceAndType(
        T source, String entityType, EntityIdGetter<T> idGetter) {
      return idGetter
          .getIdForType(context, source, entityType)
          .map(id -> Tables.immutableCell(source, entityType, id));
    }

    private ImmutableTable<String, String, Entity> buildEntityResultTable(
        Collection<EntityResultSet> entityResultSets) {
      return entityResultSets.stream()
          .flatMap(entityResultSet -> entityResultSet.results().stream())
          .map(entity -> Tables.immutableCell(entity.type(), entity.id(), entity))
          .collect(tableCollector());
    }

    Single<Set<EntityRequest>> buildEntityRequests(
        GraphQlRequestContext context, Table<?, String, String> entityIdsFromResults) {
      Map<String, Collection<String>> entityIdsByType =
          entityIdsFromResults.cellSet().stream()
              .map(
                  cell ->
                      Map.entry(
                          requireNonNull(cell.getColumnKey()), requireNonNull(cell.getValue())))
              .collect(setMultiMapCollector())
              .asMap();

      return Observable.fromIterable(entityIdsByType.entrySet())
          .flatMapSingle(
              idsForType ->
                  this.buildEntityRequestForType(
                      context, idsForType.getKey(), idsForType.getValue()))
          .collect(Collectors.toUnmodifiableSet());
    }

    Single<EntityRequest> buildEntityRequestForType(
        GraphQlRequestContext context, String entityType, Collection<String> entityIdsToFilter) {
      return buildIdFilter(context, entityType, entityIdsToFilter)
          .flatMap(
              filterArguments ->
                  resultSetRequestBuilder.build(
                      context,
                      entityType,
                      entityIdsToFilter.size(),
                      ZERO_OFFSET,
                      new InstantTimeRange(),
                      List
                          .<AttributeAssociation<AggregatableOrderArgument>>
                              of(), // Order does not matter
                      filterArguments,
                      this.entityFieldsByType.get(entityType).stream(),
                      Optional.empty()))
          .map(resultSetRequest -> new DefaultEntityRequest(entityType, resultSetRequest));
    }

    private Single<List<AttributeAssociation<FilterArgument>>> buildIdFilter(
        GraphQlRequestContext context, String entityScope, Collection<String> entityIds) {
      return filterRequestBuilder.build(
          context, entityScope, Set.of(new EntityIdFilter(entityIds, entityScope)));
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultEntityRequest implements EntityRequest {
    String entityType;
    ResultSetRequest<AggregatableOrderArgument> resultSetRequest;
    List<MetricRequest> metricRequests = Collections.emptyList(); // Only support attributes for now
    boolean fetchTotal = false; // Not needed for a single entity
    EdgeSetGroupRequest incomingEdgeRequests = new EmptyEdgeSetGroupRequest();
    EdgeSetGroupRequest outgoingEdgeRequests = new EmptyEdgeSetGroupRequest();
    boolean includeInactive = true; // When joining we want the entity regardless of time range
  }

  @Value
  @Accessors(fluent = true)
  private static class EmptyEdgeSetGroupRequest implements EdgeSetGroupRequest {
    Set<String> entityTypes = Collections.emptySet();
    Collection<AttributeRequest> attributeRequests = Collections.emptyList();
    Collection<MetricAggregationRequest> metricAggregationRequests = Collections.emptyList();
    AttributeRequest neighborIdAttribute = null;
    AttributeRequest neighborTypeAttribute = null;

    @Override
    public Single<EntityRequest> buildNeighborRequest(
        String entityType, Collection<String> neighborIds) {
      return Single.error(
          new UnsupportedOperationException(
              "Does not support fetching neighbors for joined entities"));
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class InstantTimeRange implements TimeRangeArgument {
    // Bug in gateway requires this time range be larger, even though we don't care about it
    Instant startTime = Instant.now().minus(15, ChronoUnit.MINUTES);
    Instant endTime = Instant.now();
  }

  @Value
  @Accessors(fluent = true)
  private static class EntityIdFilter implements FilterArgument {
    FilterType type = FilterType.ID;
    String key = null;
    FilterOperatorType operator = FilterOperatorType.IN;
    Collection<String> value;
    AttributeScope idType = null;
    String idScope;
  }
}
