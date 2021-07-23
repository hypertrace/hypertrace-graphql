package org.hypertrace.core.graphql.span.dao;

import static org.hypertrace.core.graphql.span.dao.DaoTestUtil.attributesAttribute;
import static org.hypertrace.core.graphql.span.dao.DaoTestUtil.spanIdAttribute;
import static org.hypertrace.core.graphql.span.dao.DaoTestUtil.spansResponse;
import static org.hypertrace.core.graphql.span.dao.DaoTestUtil.traceIdAttribute;
import static org.hypertrace.gateway.service.v1.common.Operator.AND;
import static org.hypertrace.gateway.service.v1.common.Operator.IN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Single;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.request.FilterRequestBuilder;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.span.dao.DaoTestUtil.DefaultAttributeRequest;
import org.hypertrace.core.graphql.span.dao.DaoTestUtil.DefaultResultSetRequest;
import org.hypertrace.core.graphql.span.dao.DaoTestUtil.DefaultSpanRequest;
import org.hypertrace.core.graphql.span.dao.DaoTestUtil.DefaultTimeRange;
import org.hypertrace.core.graphql.span.dao.DaoTestUtil.NormalizedFilter;
import org.hypertrace.core.graphql.span.request.SpanRequest;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.gateway.GatewayUtilsModule;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.gateway.service.v1.common.ColumnIdentifier;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.Filter;
import org.hypertrace.gateway.service.v1.common.LiteralConstant;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.common.ValueType;
import org.hypertrace.gateway.service.v1.log.events.LogEventsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SpanLogEventRequestBuilderTest {

  @Mock private FilterRequestBuilder filterRequestBuilder;

  @Mock private AttributeStore attributeStore;

  @Mock private AttributeRequestBuilder attributeRequestBuilder;

  private SpanLogEventRequestBuilder spanLogEventRequestBuilder;

  @BeforeEach
  void beforeEach() {
    Injector injector =
        Guice.createInjector(
            new GatewayUtilsModule(),
            new AbstractModule() {
              @Override
              protected void configure() {
                bind(CallCredentials.class).toInstance(mock(CallCredentials.class));
                bind(GraphQlServiceConfig.class).toInstance(mock(GraphQlServiceConfig.class));
                bind(GrpcChannelRegistry.class).toInstance(mock(GrpcChannelRegistry.class));
              }
            });

    Converter<Collection<AttributeAssociation<FilterArgument>>, Filter> filterConverter =
        injector.getInstance(
            Key.get(
                new TypeLiteral<
                    Converter<Collection<AttributeAssociation<FilterArgument>>, Filter>>() {}));

    Converter<Collection<AttributeRequest>, Set<Expression>> attributeConverter =
        injector.getInstance(
            Key.get(
                new TypeLiteral<Converter<Collection<AttributeRequest>, Set<Expression>>>() {}));

    spanLogEventRequestBuilder =
        new SpanLogEventRequestBuilder(
            attributeConverter,
            filterConverter,
            filterRequestBuilder,
            attributeStore,
            attributeRequestBuilder);

    doAnswer(
            invocation -> {
              Set<FilterArgument> filterArguments = invocation.getArgument(2, Set.class);
              FilterArgument filterArgument = filterArguments.iterator().next();
              return Single.just(
                  List.of(
                      AttributeAssociation.of(
                          spanIdAttribute.attribute(),
                          new NormalizedFilter(
                              spanIdAttribute.attribute().key(),
                              filterArgument.operator(),
                              filterArgument.value()))));
            })
        .when(filterRequestBuilder)
        .build(any(), any(), anyCollection());

    when(attributeStore.getForeignIdAttribute(any(), anyString(), anyString()))
        .thenReturn(Single.just(spanIdAttribute.attribute()));

    doAnswer(
            invocation -> {
              AttributeModel attributeModel = invocation.getArgument(0, AttributeModel.class);
              return new DefaultAttributeRequest(attributeModel);
            })
        .when(attributeRequestBuilder)
        .buildForAttribute(any());
  }

  @Test
  void testBuildRequest() {

    long startTime = System.currentTimeMillis();
    long endTime = System.currentTimeMillis() + Duration.ofHours(1).toMillis();

    Collection<AttributeRequest> logAttributeRequests =
        List.of(spanIdAttribute, traceIdAttribute, attributesAttribute);
    DefaultResultSetRequest resultSetRequest =
        new DefaultResultSetRequest(
            null,
            List.of(DaoTestUtil.eventIdAttribute),
            new DefaultTimeRange(Instant.ofEpochMilli(startTime), Instant.ofEpochMilli(endTime)),
            DaoTestUtil.eventIdAttribute,
            0,
            0,
            List.of(),
            Collections.emptyList(),
            Optional.empty());
    SpanRequest spanRequest = new DefaultSpanRequest(null, resultSetRequest, logAttributeRequests);

    LogEventsRequest expectedLogEventsRequest =
        LogEventsRequest.newBuilder()
            .setStartTimeMillis(startTime)
            .setEndTimeMillis(endTime)
            .setLimit(1000)
            .addSelection(buildAliasedSelection("spanId"))
            .addSelection(buildAliasedSelection("traceId"))
            .addSelection(buildAliasedSelection("attributes"))
            .setFilter(
                Filter.newBuilder()
                    .setOperator(AND)
                    .addChildFilter(
                        Filter.newBuilder()
                            .setLhs(buildUnaliasedSelection("spanId"))
                            .setOperator(IN)
                            .setRhs(buildStringList("span1", "span2", "span3"))))
            .build();
    assertEquals(
        expectedLogEventsRequest,
        spanLogEventRequestBuilder.buildLogEventsRequest(spanRequest, spansResponse).blockingGet());
  }

  @Test
  void testBuildRequest_addSpanId() {
    long startTime = System.currentTimeMillis();
    long endTime = System.currentTimeMillis() + Duration.ofHours(1).toMillis();

    Collection<AttributeRequest> logAttributeRequests = List.of(traceIdAttribute);
    DefaultResultSetRequest resultSetRequest =
        new DefaultResultSetRequest(
            null,
            List.of(DaoTestUtil.eventIdAttribute),
            new DefaultTimeRange(Instant.ofEpochMilli(startTime), Instant.ofEpochMilli(endTime)),
            DaoTestUtil.eventIdAttribute,
            0,
            0,
            List.of(),
            Collections.emptyList(),
            Optional.empty());
    SpanRequest spanRequest = new DefaultSpanRequest(null, resultSetRequest, logAttributeRequests);

    LogEventsRequest expectedLogEventsRequest =
        LogEventsRequest.newBuilder()
            .setStartTimeMillis(startTime)
            .setEndTimeMillis(endTime)
            .setLimit(1000)
            .addSelection(buildAliasedSelection("spanId"))
            .addSelection(buildAliasedSelection("traceId"))
            .setFilter(
                Filter.newBuilder()
                    .setOperator(AND)
                    .addChildFilter(
                        Filter.newBuilder()
                            .setLhs(buildUnaliasedSelection("spanId"))
                            .setOperator(IN)
                            .setRhs(buildStringList("span1", "span2", "span3"))))
            .build();
    assertEquals(
        expectedLogEventsRequest,
        spanLogEventRequestBuilder.buildLogEventsRequest(spanRequest, spansResponse).blockingGet());
  }

  Expression buildAliasedSelection(String name) {
    return Expression.newBuilder()
        .setColumnIdentifier(ColumnIdentifier.newBuilder().setColumnName(name).setAlias(name))
        .build();
  }

  Expression buildUnaliasedSelection(String name) {
    return Expression.newBuilder()
        .setColumnIdentifier(ColumnIdentifier.newBuilder().setColumnName(name))
        .build();
  }

  Expression buildStringList(String... values) {
    return Expression.newBuilder()
        .setLiteral(
            LiteralConstant.newBuilder()
                .setValue(
                    Value.newBuilder()
                        .setValueType(ValueType.STRING_ARRAY)
                        .addAllStringArray(List.of(values))))
        .build();
  }
}
