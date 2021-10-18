package org.hypertrace.graphql.entity.joiner;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.request.FilterRequestBuilder;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequestBuilder;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;
import org.hypertrace.graphql.entity.dao.EntityDao;
import org.hypertrace.graphql.entity.joiner.EntityJoiner.EntityIdGetter;
import org.hypertrace.graphql.entity.request.EntityLabelRequest;
import org.hypertrace.graphql.entity.request.EntityLabelRequestBuilder;
import org.hypertrace.graphql.entity.request.EntityRequest;
import org.hypertrace.graphql.entity.schema.EdgeResultSet;
import org.hypertrace.graphql.entity.schema.Entity;
import org.hypertrace.graphql.entity.schema.EntityResultSet;
import org.hypertrace.graphql.entity.schema.EntityType;
import org.hypertrace.graphql.entity.schema.argument.EntityTypeStringArgument;
import org.hypertrace.graphql.label.schema.LabelResultSet;
import org.hypertrace.graphql.metric.schema.MetricContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultEntityJoinerBuilderTest {
  private static final String FIRST_ENTITY_TYPE = "first";
  private static final String SECOND_ENTITY_TYPE = "second";

  @Mock EntityDao mockEntityDao;
  @Mock GraphQlSelectionFinder mockSelectionFinder;
  @Mock ArgumentDeserializer mockDeserializer;
  @Mock ResultSetRequestBuilder mockResultSetRequestBuilder;
  @Mock FilterRequestBuilder mockFilterRequestBuilder;
  @Mock AttributeRequestBuilder attributeRequestBuilder;
  @Mock GraphQlRequestContext mockRequestContext;
  @Mock DataFetchingFieldSelectionSet mockSelectionSet;
  @Mock AttributeAssociation<FilterArgument> mockFilter;
  @Mock ResultSetRequest mockResultSetRequest;
  @Mock EntityLabelRequestBuilder mockEntityLabelRequestBuilder;

  Scheduler testScheduler = Schedulers.single();

  DefaultEntityJoinerBuilder entityJoinerBuilder;

  @BeforeEach
  void beforeEach() {
    this.entityJoinerBuilder =
        new DefaultEntityJoinerBuilder(
            mockEntityDao,
            mockSelectionFinder,
            mockDeserializer,
            mockResultSetRequestBuilder,
            mockFilterRequestBuilder,
            attributeRequestBuilder,
            testScheduler,
            mockEntityLabelRequestBuilder);
  }

  @Test
  void fetchesEntitiesOfMultipleTypesInBatches() {
    Entity firstTypeFirstIdEntity = new TestEntity(FIRST_ENTITY_TYPE, "first-id-1");
    Entity firstTypeSecondIdEntity = new TestEntity(FIRST_ENTITY_TYPE, "first-id-2");
    Entity secondTypeFirstIdEntity = new TestEntity(SECOND_ENTITY_TYPE, "second-id-1");
    Entity secondTypeSecondIdEntity = new TestEntity(SECOND_ENTITY_TYPE, "second-id-2");
    TestJoinSource firstJoinSource = new TestJoinSource("first-id-1", null);
    TestJoinSource secondJoinSource = new TestJoinSource("first-id-2", null);
    TestJoinSource thirdJoinSource = new TestJoinSource(null, "second-id-1");
    TestJoinSource fourthJoinSource = new TestJoinSource("first-id-1", "second-id-2");
    Table<TestJoinSource, String, Entity> expected =
        ImmutableTable.<TestJoinSource, String, Entity>builder()
            .put(firstJoinSource, FIRST_ENTITY_TYPE, firstTypeFirstIdEntity)
            .put(secondJoinSource, FIRST_ENTITY_TYPE, firstTypeSecondIdEntity)
            .put(thirdJoinSource, SECOND_ENTITY_TYPE, secondTypeFirstIdEntity)
            .put(fourthJoinSource, FIRST_ENTITY_TYPE, firstTypeFirstIdEntity)
            .put(fourthJoinSource, SECOND_ENTITY_TYPE, secondTypeSecondIdEntity)
            .build();

    List<TestJoinSource> joinSources =
        List.of(firstJoinSource, secondJoinSource, thirdJoinSource, fourthJoinSource);

    mockRequestedEntityFields(
        Map.of(
            FIRST_ENTITY_TYPE,
            mock(SelectedField.class),
            SECOND_ENTITY_TYPE,
            mock(SelectedField.class)),
        "pathToEntity");

    when(this.mockEntityLabelRequestBuilder.buildLabelRequestIfPresentInAnyEntity(
            eq(mockRequestContext), any(), any()))
        .thenReturn(Single.just(Optional.empty()));
    mockRequestBuilding();
    mockResult(
        Map.of(
            FIRST_ENTITY_TYPE,
            List.of(firstTypeFirstIdEntity, firstTypeSecondIdEntity),
            SECOND_ENTITY_TYPE,
            List.of(secondTypeFirstIdEntity, secondTypeSecondIdEntity)));

    EntityJoiner joiner =
        this.entityJoinerBuilder
            .build(this.mockRequestContext, this.mockSelectionSet, List.of("pathToEntity"))
            .blockingGet();

    assertEquals(
        expected, joiner.joinEntities(joinSources, new TestJoinSourceIdGetter()).blockingGet());
  }

  @Test
  void doesNotFetchIfNoIdsFound() {

    TestJoinSource firstJoinSource = new TestJoinSource(null, null);
    List<TestJoinSource> joinSources = List.of(firstJoinSource);

    mockRequestedEntityFields(Map.of(FIRST_ENTITY_TYPE, mock(SelectedField.class)), "pathToEntity");

    EntityJoiner joiner =
        this.entityJoinerBuilder
            .build(this.mockRequestContext, this.mockSelectionSet, List.of("pathToEntity"))
            .blockingGet();

    assertEquals(
        ImmutableTable.of(),
        joiner.joinEntities(joinSources, new TestJoinSourceIdGetter()).blockingGet());

    verify(this.mockEntityDao, never()).getEntities(any());
  }

  @Test
  void doesNotFetchIfNoEntitiesRequested() {
    TestJoinSource firstJoinSource = new TestJoinSource("first-id", null);
    List<TestJoinSource> joinSources = List.of(firstJoinSource);

    mockRequestedEntityFields(Map.of(), "pathToEntity");

    EntityJoiner joiner =
        this.entityJoinerBuilder
            .build(this.mockRequestContext, this.mockSelectionSet, List.of("pathToEntity"))
            .blockingGet();

    assertEquals(
        ImmutableTable.of(),
        joiner.joinEntities(joinSources, new TestJoinSourceIdGetter()).blockingGet());

    verify(this.mockEntityDao, never()).getEntities(any());
  }

  @Test
  void fetchesEntitiesWithLabels() {
    Entity firstTypeFirstIdEntity = new TestEntity(FIRST_ENTITY_TYPE, "first-id-1");
    Entity firstTypeSecondIdEntity = new TestEntity(FIRST_ENTITY_TYPE, "first-id-2");
    Entity secondTypeFirstIdEntity = new TestEntity(SECOND_ENTITY_TYPE, "second-id-1");
    Entity secondTypeSecondIdEntity = new TestEntity(SECOND_ENTITY_TYPE, "second-id-2");
    TestJoinSource firstJoinSource = new TestJoinSource("first-id-1", null);
    TestJoinSource secondJoinSource = new TestJoinSource("first-id-2", null);
    TestJoinSource thirdJoinSource = new TestJoinSource(null, "second-id-1");
    TestJoinSource fourthJoinSource = new TestJoinSource("first-id-1", "second-id-2");
    Table<TestJoinSource, String, Entity> expected =
        ImmutableTable.<TestJoinSource, String, Entity>builder()
            .put(firstJoinSource, FIRST_ENTITY_TYPE, firstTypeFirstIdEntity)
            .put(secondJoinSource, FIRST_ENTITY_TYPE, firstTypeSecondIdEntity)
            .put(thirdJoinSource, SECOND_ENTITY_TYPE, secondTypeFirstIdEntity)
            .put(fourthJoinSource, FIRST_ENTITY_TYPE, firstTypeFirstIdEntity)
            .put(fourthJoinSource, SECOND_ENTITY_TYPE, secondTypeSecondIdEntity)
            .build();

    List<TestJoinSource> joinSources =
        List.of(firstJoinSource, secondJoinSource, thirdJoinSource, fourthJoinSource);

    SelectedField mockSelectionField1 = mock(SelectedField.class);
    SelectedField mockSelectionField2 = mock(SelectedField.class);
    EntityLabelRequest mockEntityLabelRequest = mock(EntityLabelRequest.class);
    when(mockEntityLabelRequestBuilder.buildLabelRequestIfPresentInAnyEntity(
            eq(mockRequestContext), eq(FIRST_ENTITY_TYPE), eq(Set.of(mockSelectionField1))))
        .thenReturn(Single.just(Optional.of(mockEntityLabelRequest)));
    when(mockEntityLabelRequestBuilder.buildLabelRequestIfPresentInAnyEntity(
            eq(mockRequestContext), eq(SECOND_ENTITY_TYPE), eq(Set.of(mockSelectionField2))))
        .thenReturn(Single.just(Optional.empty()));
    mockRequestedEntityFields(
        Map.of(
            FIRST_ENTITY_TYPE, mockSelectionField1,
            SECOND_ENTITY_TYPE, mockSelectionField2),
        "pathToEntity");

    mockRequestBuilding();
    mockResult(
        Map.of(
            FIRST_ENTITY_TYPE,
            List.of(firstTypeFirstIdEntity, firstTypeSecondIdEntity),
            SECOND_ENTITY_TYPE,
            List.of(secondTypeFirstIdEntity, secondTypeSecondIdEntity)));

    EntityJoiner joiner =
        this.entityJoinerBuilder
            .build(this.mockRequestContext, this.mockSelectionSet, List.of("pathToEntity"))
            .blockingGet();

    assertEquals(
        expected, joiner.joinEntities(joinSources, new TestJoinSourceIdGetter()).blockingGet());

    verify(mockEntityDao, times(1))
        .getEntities(
            argThat(
                request ->
                    request.entityType().equals(FIRST_ENTITY_TYPE)
                        && request.labelRequest().get().equals(mockEntityLabelRequest)));
    verify(mockEntityDao, times(1))
        .getEntities(
            argThat(
                request ->
                    request.entityType().equals(SECOND_ENTITY_TYPE)
                        && request.labelRequest().isEmpty()));
  }

  private void mockRequestedEntityFields(
      Map<String, SelectedField> selectedFieldsByEntityType, String location) {

    when(mockSelectionFinder.findSelections(
            mockSelectionSet,
            SelectionQuery.builder().selectionPath(List.of(location, "entity")).build()))
        .thenReturn(selectedFieldsByEntityType.values().stream());

    selectedFieldsByEntityType.forEach(
        (entityType, entityField) -> {
          Map<String, Object> argsForField = Map.of(entityType, entityType);
          when(entityField.getArguments()).thenReturn(argsForField);

          when(mockDeserializer.deserializePrimitive(argsForField, EntityTypeStringArgument.class))
              .thenReturn(Optional.of(entityType));
        });
  }

  private void mockRequestBuilding() {
    when(mockFilterRequestBuilder.build(
            eq(mockRequestContext), any(String.class), any(Collection.class)))
        .thenReturn(Single.just(List.of(mockFilter)));

    when(mockResultSetRequestBuilder.build(
            eq(mockRequestContext),
            any(String.class),
            eq(2),
            eq(0),
            any(TimeRangeArgument.class),
            eq(emptyList()),
            eq(List.of(mockFilter)),
            any(Stream.class),
            eq(Optional.empty())))
        .thenReturn(Single.just(mockResultSetRequest));
  }

  private void mockResult(Map<String, List<Entity>> resultsByEntityType) {
    when(mockEntityDao.getEntities(any(EntityRequest.class)))
        .thenAnswer(
            invocation -> {
              String entityType = invocation.getArgument(0, EntityRequest.class).entityType();
              if (resultsByEntityType.containsKey(entityType)) {
                return Single.just(new TestEntityResultSet(resultsByEntityType.get(entityType)));
              }

              return Single.error(new UnsupportedOperationException());
            });
  }

  @Value
  private static class TestJoinSource {
    String firstEntityId;
    String secondEntityId;
  }

  private static class TestJoinSourceIdGetter implements EntityIdGetter<TestJoinSource> {
    @Override
    public Maybe<String> getIdForType(
        GraphQlRequestContext requestContext, TestJoinSource source, String entityType) {
      if (entityType.equals(FIRST_ENTITY_TYPE)) {
        return Maybe.fromOptional(Optional.ofNullable(source.getFirstEntityId()));
      }
      if (entityType.equals(SECOND_ENTITY_TYPE)) {
        return Maybe.fromOptional(Optional.ofNullable(source.getSecondEntityId()));
      }
      return Maybe.empty();
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class TestEntityResultSet implements EntityResultSet {
    List<Entity> results;
    long count = 0;
    long total = 0;
  }

  @Value
  @Accessors(fluent = true)
  private static class TestEntity implements Entity {
    String type;
    String id;

    @Override
    public Object attribute(String key) {
      return null;
    }

    @Override
    public EdgeResultSet incomingEdges(EntityType neighborType, String neighborScope) {
      return null;
    }

    @Override
    public EdgeResultSet outgoingEdges(EntityType neighborType, String neighborScope) {
      return null;
    }

    @Override
    public MetricContainer metric(String key) {
      return null;
    }

    @Override
    public LabelResultSet labels() {
      return null;
    }
  }
}
