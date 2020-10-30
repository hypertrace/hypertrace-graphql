package org.hypertrace.core.graphql.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.hypertrace.core.graphql.context.ContextualCachingKey;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings
class CachingAttributeStoreTest {
  @Mock AttributeClient mockAttributeClient;
  @Mock IdLookup mockIdLookup;

  private final AttributeModel traceAttribute =
      DefaultAttributeModel.builder().scope("TRACE").key("traceKey").build();
  private final AttributeModel spanAttribute =
      DefaultAttributeModel.builder().scope("SPAN").key("spanKey").build();

  private AttributeStore attributeStore;

  @BeforeEach
  void beforeEach() {
    this.attributeStore = new CachingAttributeStore(this.mockAttributeClient, this.mockIdLookup);
  }

  @Test
  void cachesConsecutiveGetAllCallsWithSameCacheKey() {
    List<AttributeModel> expected = List.of(spanAttribute, traceAttribute);
    GraphQlRequestContext context = this.buildNewMockContext();
    when(this.mockAttributeClient.queryAll(eq(context)))
        .thenReturn(Observable.fromIterable(expected));
    assertEquals(expected, this.attributeStore.getAll(context).blockingGet());
    assertEquals(expected, this.attributeStore.getAll(context).blockingGet());

    verify(this.mockAttributeClient, times(1)).queryAll(any());
  }

  @Test
  void cachesConsecutiveGetCallsWithSameCacheKey() {
    GraphQlRequestContext context = this.buildNewMockContext();
    when(this.mockAttributeClient.queryAll(eq(context)))
        .thenReturn(Observable.just(spanAttribute, traceAttribute));
    AttributeModel spanResult =
        this.attributeStore
            .get(context, this.spanAttribute.scope(), this.spanAttribute.key())
            .blockingGet();
    assertEquals(this.spanAttribute, spanResult);

    AttributeModel traceResult =
        this.attributeStore
            .get(context, this.traceAttribute.scope(), this.traceAttribute.key())
            .blockingGet();
    assertEquals(this.traceAttribute, traceResult);

    verify(this.mockAttributeClient, times(1)).queryAll(any());
  }

  @Test
  void throwsErrorIfNoKeyMatch() {
    GraphQlRequestContext context = this.buildNewMockContext();
    when(this.mockAttributeClient.queryAll(eq(context))).thenReturn(Observable.empty());

    assertThrows(
        NoSuchElementException.class,
        this.attributeStore.get(context, "SPAN", "nonExistentKey")::blockingGet);
  }

  @Test
  void lazilyFetchesAndCachesResultsOnSubscribe() {
    GraphQlRequestContext context = this.buildNewMockContext();
    // Mutable, so we can clear it after getting a Single
    List<AttributeModel> attributesList = new ArrayList<>(List.of(this.spanAttribute));

    when(this.mockAttributeClient.queryAll(eq(context)))
        .thenReturn(Observable.defer(() -> Observable.fromIterable(attributesList)));

    Single<List<AttributeModel>> resultSingle = this.attributeStore.getAll(context);
    attributesList.clear();
    assertEquals(Collections.emptyList(), resultSingle.blockingGet());

    attributesList.add(this.spanAttribute);
    assertEquals(Collections.emptyList(), resultSingle.blockingGet());

    // Now, make a new request
    assertEquals(Collections.emptyList(), this.attributeStore.getAll(context).blockingGet());
  }

  @Test
  void supportsMultipleConcurrentCacheKeys() {
    GraphQlRequestContext firstContext = this.buildNewMockContext();
    GraphQlRequestContext secondContext = this.buildNewMockContext();
    AttributeModel alternateSpanAttribute =
        DefaultAttributeModel.builder()
            .scope(this.spanAttribute.scope())
            .key(this.spanAttribute.key())
            .displayName("some new display name")
            .build();

    when(this.mockAttributeClient.queryAll(eq(firstContext)))
        .thenReturn(Observable.just(spanAttribute));
    when(this.mockAttributeClient.queryAll(eq(secondContext)))
        .thenReturn(Observable.just(alternateSpanAttribute));

    AttributeModel firstSpanResult =
        this.attributeStore
            .get(firstContext, this.spanAttribute.scope(), this.spanAttribute.key())
            .blockingGet();
    assertEquals(this.spanAttribute, firstSpanResult);
    // Fetch it a second time to make sure it's not just the first call
    assertEquals(
        firstSpanResult,
        this.attributeStore
            .get(firstContext, this.spanAttribute.scope(), this.spanAttribute.key())
            .blockingGet());

    AttributeModel secondSpanResult =
        this.attributeStore
            .get(secondContext, this.spanAttribute.scope(), this.spanAttribute.key())
            .blockingGet();

    assertEquals(alternateSpanAttribute, secondSpanResult);
    assertNotEquals(firstSpanResult, secondSpanResult);

    verify(this.mockAttributeClient, times(1)).queryAll(eq(firstContext));
    verify(this.mockAttributeClient, times(1)).queryAll(eq(secondContext));
  }

  @Test
  void supportsAndCachesMultiValuedGet() {
    GraphQlRequestContext context = this.buildNewMockContext();
    when(this.mockAttributeClient.queryAll(eq(context))).thenReturn(Observable.just(spanAttribute));
    this.attributeStore
        .get(context, this.spanAttribute.scope(), this.spanAttribute.key())
        .blockingSubscribe();

    Map<String, AttributeModel> mapResult =
        this.attributeStore
            .get(context, this.spanAttribute.scope(), List.of(this.spanAttribute.key()))
            .blockingGet();
    assertEquals(1, mapResult.size());
    assertTrue(mapResult.containsKey(this.spanAttribute.key()));
    assertEquals(this.spanAttribute, mapResult.get(this.spanAttribute.key()));
    verify(this.mockAttributeClient, times(1)).queryAll(eq(context));
  }

  @Test
  void supportsAndCachesIdLookup() {
    GraphQlRequestContext context = this.buildNewMockContext();
    DefaultAttributeModel spanIdAttribute =
        DefaultAttributeModel.builder().scope("SPAN").key("id").build();

    when(this.mockAttributeClient.queryAll(eq(context)))
        .thenReturn(Observable.just(spanIdAttribute));
    when(this.mockIdLookup.idKey(context, "SPAN")).thenReturn(Maybe.just("id"));

    assertEquals(
        spanIdAttribute,
        this.attributeStore.getIdAttribute(context, spanIdAttribute.scope()).blockingGet());

    assertEquals(
        spanIdAttribute,
        this.attributeStore.getIdAttribute(context, spanIdAttribute.scope()).blockingGet());

    verify(this.mockAttributeClient, times(1)).queryAll(eq(context));
  }

  @Test
  void returnsErrorForMissingIdMapping() {
    when(this.mockIdLookup.idKey(any(), any())).thenReturn(Maybe.empty());
    assertThrows(
        NoSuchElementException.class,
        this.attributeStore.getIdAttribute(null, "SPAN")::blockingGet);
  }

  @Test
  void supportsForeignIdLookup() {
    GraphQlRequestContext context = this.buildNewMockContext();
    DefaultAttributeModel spanTraceIdAttribute =
        DefaultAttributeModel.builder().scope("SPAN").key("traceId").build();

    when(this.mockIdLookup.foreignIdKey(context, "SPAN", "TRACE"))
        .thenReturn(Maybe.just("traceId"));
    when(this.mockAttributeClient.queryAll(eq(context)))
        .thenReturn(Observable.just(spanTraceIdAttribute));

    assertEquals(
        spanTraceIdAttribute,
        this.attributeStore.getForeignIdAttribute(context, "SPAN", "TRACE").blockingGet());
  }

  @Test
  void retriesOnError() {
    GraphQlRequestContext context = this.buildNewMockContext();
    when(this.mockAttributeClient.queryAll(eq(context)))
        .thenReturn(Observable.error(IllegalStateException::new));

    assertThrows(IllegalStateException.class, this.attributeStore.getAll(context)::blockingGet);

    when(this.mockAttributeClient.queryAll(eq(context)))
        .thenReturn(Observable.just(this.spanAttribute));

    assertEquals(List.of(this.spanAttribute), this.attributeStore.getAll(context).blockingGet());

    verify(this.mockAttributeClient, times(2)).queryAll(any());
  }

  private GraphQlRequestContext buildNewMockContext() {
    ContextualCachingKey mockCachingKey = mock(ContextualCachingKey.class);
    GraphQlRequestContext mockContext = mock(GraphQlRequestContext.class);
    when(mockCachingKey.getContext()).thenReturn(mockContext);
    when(mockContext.getCachingKey()).thenReturn(mockCachingKey);
    return mockContext;
  }
}
