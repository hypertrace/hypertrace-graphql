package org.hypertrace.core.graphql.span.dao;

import static org.hypertrace.core.graphql.span.dao.DaoTestUtil.attributesAttribute;
import static org.hypertrace.core.graphql.span.dao.DaoTestUtil.logEventsResponse;
import static org.hypertrace.core.graphql.span.dao.DaoTestUtil.spanIdAttribute;
import static org.hypertrace.core.graphql.span.dao.DaoTestUtil.spansResponse;
import static org.hypertrace.core.graphql.span.dao.DaoTestUtil.traceIdAttribute;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.gateway.service.v1.common.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SpanLogEventResponseConverterTest {

  @Mock
  BiConverter<Collection<AttributeRequest>, Map<String, Value>, Map<AttributeExpression, Object>>
      attributeMapConverter;

  @Mock AttributeStore attributeStore;
  @Mock GraphQlRequestContext requestContext;
  @Mock AttributeRequestBuilder attributeRequestBuilder;

  private SpanLogEventResponseConverter spanLogEventResponseConverter;

  @BeforeEach
  void beforeEach() {
    spanLogEventResponseConverter =
        new SpanLogEventResponseConverter(
            attributeMapConverter, attributeStore, attributeRequestBuilder);
  }

  @Test
  void testBuildResponse() {
    Collection<AttributeRequest> attributeRequests =
        List.of(spanIdAttribute, traceIdAttribute, attributesAttribute);

    when(attributeStore.getForeignIdAttribute(any(), anyString(), anyString()))
        .thenReturn(Single.just(spanIdAttribute.attributeExpression().attribute()));
    when(attributeRequestBuilder.buildForAttribute(
            spanIdAttribute.attributeExpression().attribute()))
        .thenReturn(spanIdAttribute);

    doAnswer(
            invocation -> {
              Map<String, Value> map = invocation.getArgument(1, Map.class);
              return Single.just(
                  map.entrySet().stream()
                      .collect(
                          Collectors.toMap(
                              valueEntry ->
                                  AttributeExpression.forAttributeKey(valueEntry.getKey()),
                              valueEntry -> valueEntry.getValue().getString())));
            })
        .when(attributeMapConverter)
        .convert(anyCollection(), anyMap());

    SpanLogEventsResponse response =
        spanLogEventResponseConverter
            .buildResponse(requestContext, attributeRequests, spansResponse, logEventsResponse)
            .blockingGet();

    assertEquals(spansResponse, response.spansResponse());
    assertEquals(Set.of("span1", "span2"), response.spanIdToLogEvents().keySet());
  }

  @Test
  void testBuildResponse_spanIdNotRequested() {
    Collection<AttributeRequest> attributeRequests = List.of(traceIdAttribute, attributesAttribute);

    when(attributeStore.getForeignIdAttribute(any(), anyString(), anyString()))
        .thenReturn(Single.just(spanIdAttribute.attributeExpression().attribute()));
    when(attributeRequestBuilder.buildForAttribute(
            spanIdAttribute.attributeExpression().attribute()))
        .thenReturn(spanIdAttribute);

    doAnswer(
            invocation -> {
              Map<String, Value> map = invocation.getArgument(1, Map.class);
              return Single.just(
                  map.entrySet().stream()
                      .collect(
                          Collectors.toMap(
                              valueEntry ->
                                  AttributeExpression.forAttributeKey(valueEntry.getKey()),
                              valueEntry -> valueEntry.getValue().getString())));
            })
        .when(attributeMapConverter)
        .convert(anyCollection(), anyMap());

    SpanLogEventsResponse response =
        spanLogEventResponseConverter
            .buildResponse(requestContext, attributeRequests, spansResponse, logEventsResponse)
            .blockingGet();

    assertEquals(spansResponse, response.spansResponse());
    assertEquals(Set.of("span1", "span2"), response.spanIdToLogEvents().keySet());
  }
}
