package org.hypertrace.core.graphql.attributes;

import static org.hypertrace.core.graphql.attributes.IdMapping.forForeignId;
import static org.hypertrace.core.graphql.attributes.IdMapping.forId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Collections;
import java.util.Set;
import org.hypertrace.core.graphql.context.ContextualCachingKey;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.junit.jupiter.api.Test;

class IdLookupTest {

  @Test
  void canLookupIds() {
    GraphQlRequestContext mockContext = this.buildNewMockContext();
    IdLookup idLookup =
        new IdLookup(
            Set.of(
                forId("SPAN", "s-id"),
                forId("TRACE", "t-id"),
                forForeignId("SPAN", "TRACE", "s-t-id")),
            Collections.emptySet(),
            Schedulers.single());

    assertEquals("s-id", idLookup.idKey(mockContext, "SPAN").blockingGet());
    assertEquals("t-id", idLookup.idKey(mockContext, "TRACE").blockingGet());
    assertEquals("s-t-id", idLookup.foreignIdKey(mockContext, "SPAN", "TRACE").blockingGet());
    assertTrue(idLookup.foreignIdKey(mockContext, "TRACE", "SPAN").isEmpty().blockingGet());
  }

  @Test
  void supportsMultipleAsyncLoaders() {

    GraphQlRequestContext mockContext = this.buildNewMockContext();

    IdLookup idLookup =
        new IdLookup(
            Set.of(forId("SCOPE_ONE", "1-id")),
            Set.of(
                unused ->
                    Observable.just(
                        forId("SCOPE_TWO", "2-id"),
                        forForeignId("SCOPE_TWO", "SCOPE_THREE", "3-id")),
                unused -> Observable.just(forId("SCOPE_THREE", "3-id"))),
            Schedulers.single());

    assertEquals("1-id", idLookup.idKey(mockContext, "SCOPE_ONE").blockingGet());
    assertEquals("2-id", idLookup.idKey(mockContext, "SCOPE_TWO").blockingGet());
    assertEquals(
        "3-id", idLookup.foreignIdKey(mockContext, "SCOPE_TWO", "SCOPE_THREE").blockingGet());
    assertEquals("3-id", idLookup.idKey(mockContext, "SCOPE_THREE").blockingGet());
    assertTrue(idLookup.idKey(mockContext, "SCOPE_FOUR").isEmpty().blockingGet());
  }

  @Test
  void cachesLoaderResults() {
    GraphQlRequestContext mockContext = this.buildNewMockContext();
    GraphQlRequestContext otherContext = this.buildNewMockContext();
    IdMappingLoader mappingLoader = mock(IdMappingLoader.class);
    when(mappingLoader.loadMappings(mockContext))
        .thenReturn(Observable.just(forId("SCOPE_ONE", "1-id")));
    when(mappingLoader.loadMappings(otherContext))
        .thenReturn(Observable.just(forId("SCOPE_ONE", "1-id-other")));
    IdLookup idLookup =
        new IdLookup(Collections.emptySet(), Set.of(mappingLoader), Schedulers.single());

    assertEquals("1-id", idLookup.idKey(mockContext, "SCOPE_ONE").blockingGet());
    assertEquals("1-id-other", idLookup.idKey(otherContext, "SCOPE_ONE").blockingGet());
    assertEquals("1-id", idLookup.idKey(mockContext, "SCOPE_ONE").blockingGet());
    assertEquals("1-id-other", idLookup.idKey(otherContext, "SCOPE_ONE").blockingGet());
    verify(mappingLoader, times(1)).loadMappings(mockContext);
    verify(mappingLoader, times(1)).loadMappings(otherContext);
  }

  @Test
  void retriesOnError() {
    GraphQlRequestContext mockContext = this.buildNewMockContext();

    IdMappingLoader mappingLoader = mock(IdMappingLoader.class);
    when(mappingLoader.loadMappings(mockContext))
        .thenReturn(Observable.error(IllegalStateException::new));
    IdLookup idLookup =
        new IdLookup(Collections.emptySet(), Set.of(mappingLoader), Schedulers.single());

    assertThrows(
        IllegalStateException.class, idLookup.idKey(mockContext, "SCOPE_ONE")::blockingGet);

    when(mappingLoader.loadMappings(mockContext))
        .thenReturn(Observable.just(forId("SCOPE_ONE", "1-id")));

    assertEquals("1-id", idLookup.idKey(mockContext, "SCOPE_ONE").blockingGet());
  }

  private GraphQlRequestContext buildNewMockContext() {
    ContextualCachingKey mockCachingKey = mock(ContextualCachingKey.class);
    GraphQlRequestContext mockContext = mock(GraphQlRequestContext.class);
    when(mockCachingKey.getContext()).thenReturn(mockContext);
    when(mockContext.getCachingKey()).thenReturn(mockCachingKey);
    return mockContext;
  }
}
