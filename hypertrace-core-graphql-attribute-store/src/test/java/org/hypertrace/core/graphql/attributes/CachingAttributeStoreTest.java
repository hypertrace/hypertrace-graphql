package org.hypertrace.core.graphql.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.hypertrace.core.attribute.service.cachingclient.CachingAttributeClient;
import org.hypertrace.core.attribute.service.v1.AttributeMetadata;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings
class CachingAttributeStoreTest {
  @Mock CachingAttributeClient mockAttributeClient;

  @Mock(answer = Answers.CALLS_REAL_METHODS)
  GrpcContextBuilder mockGrpcContextBuilder;

  @Mock AttributeModelTranslator mockAttributeModeTranslator;

  @Mock IdLookup mockIdLookup;

  @Mock GraphQlRequestContext mockContext;

  private AttributeStore attributeStore;

  @BeforeEach
  void beforeEach() {
    this.attributeStore =
        new CachingAttributeStore(
            this.mockIdLookup,
            this.mockGrpcContextBuilder,
            this.mockAttributeModeTranslator,
            this.mockAttributeClient);
  }

  @Test
  void supportsBasicRead() {
    AttributeModel spanIdAttribute =
        DefaultAttributeModel.builder().scope("SPAN").key("id").build();
    AttributeMetadata spanIdResponse =
        AttributeMetadata.newBuilder().setScopeString("SPAN").setKey("id").build();

    when(this.mockAttributeClient.get("SPAN", "id")).thenReturn(Single.just(spanIdResponse));
    when(this.mockAttributeModeTranslator.translate(spanIdResponse))
        .thenReturn(Optional.of(spanIdAttribute));

    assertEquals(spanIdAttribute, this.attributeStore.get(mockContext, "SPAN", "id").blockingGet());

    when(this.mockAttributeClient.getAll()).thenReturn(Single.just(List.of(spanIdResponse)));

    assertEquals(List.of(spanIdAttribute), this.attributeStore.getAll(mockContext).blockingGet());
  }

  @Test
  void throwsErrorIfNoKeyMatch() {
    when(this.mockAttributeClient.get(any(), any()))
        .thenReturn(Single.error(new NoSuchElementException()));

    assertThrows(
        NoSuchElementException.class,
        this.attributeStore.get(mockContext, "SPAN", "nonExistentKey")::blockingGet);
  }

  @Test
  void throwsErrorForMissingIdMapping() {
    when(this.mockIdLookup.idKey(any(), any())).thenReturn(Maybe.empty());
    assertThrows(
        NoSuchElementException.class,
        this.attributeStore.getIdAttribute(null, "SPAN")::blockingGet);
  }

  @Test
  void supportsForeignIdLookup() {
    DefaultAttributeModel spanTraceIdAttribute =
        DefaultAttributeModel.builder().scope("SPAN").key("traceId").build();
    AttributeMetadata spanTraceIdResponse =
        AttributeMetadata.newBuilder().setScopeString("SPAN").setKey("traceID").build();

    when(this.mockIdLookup.foreignIdKey(mockContext, "SPAN", "TRACE"))
        .thenReturn(Maybe.just("traceId"));
    when(this.mockAttributeClient.get("SPAN", "traceId"))
        .thenReturn(Single.just(spanTraceIdResponse));
    when(this.mockAttributeModeTranslator.translate(spanTraceIdResponse))
        .thenReturn(Optional.of(spanTraceIdAttribute));

    assertEquals(
        spanTraceIdAttribute,
        this.attributeStore.getForeignIdAttribute(mockContext, "SPAN", "TRACE").blockingGet());
  }
}
