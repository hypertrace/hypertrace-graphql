package org.hypertrace.core.graphql.log.event.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.grpc.CallCredentials;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.hypertrace.core.graphql.attributes.AttributeModelType;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.gateway.GatewayUtilsModule;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.Filter;
import org.hypertrace.gateway.service.v1.common.OrderByExpression;
import org.hypertrace.gateway.service.v1.log.events.LogEventsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GatewayServiceLogEventsRequestBuilderTest extends BaseDaoTest {

  private GatewayServiceLogEventsRequestBuilder requestBuilder;

  @BeforeEach
  void setup() {
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
    Converter<List<AttributeAssociation<OrderArgument>>, List<OrderByExpression>> orderConverter =
        injector.getInstance(
            Key.get(
                new TypeLiteral<
                    Converter<
                        List<AttributeAssociation<OrderArgument>>, List<OrderByExpression>>>() {}));
    Converter<Collection<AttributeRequest>, Set<Expression>> attributeConverter =
        injector.getInstance(
            Key.get(
                new TypeLiteral<Converter<Collection<AttributeRequest>, Set<Expression>>>() {}));

    requestBuilder =
        new GatewayServiceLogEventsRequestBuilder(
            filterConverter, orderConverter, attributeConverter);
  }

  @Test
  void testBuildRequest() {
    long startTime = System.currentTimeMillis();
    long endTime = System.currentTimeMillis() + Duration.ofHours(1).toMillis();
    Collection<AttributeRequest> attributeRequests =
        List.of(
            new DefaultAttributeRequest(
                AttributeAssociation.of(
                    new DefaultAttributeModel(
                        "traceId",
                        "LOG_EVENT",
                        "traceId",
                        "Trace Id",
                        AttributeModelType.STRING,
                        "",
                        false,
                        false,
                        Collections.emptyList(),
                        false,
                        false),
                    AttributeExpression.forAttributeKey("traceId"))),
            new DefaultAttributeRequest(
                AttributeAssociation.of(
                    new DefaultAttributeModel(
                        "timestamp",
                        "LOG_EVENT",
                        "timestamp",
                        "Timestamp",
                        AttributeModelType.TIMESTAMP,
                        "",
                        false,
                        false,
                        Collections.emptyList(),
                        false,
                        false),
                    AttributeExpression.forAttributeKey("timestamp"))));
    DefaultLogEventRequest defaultLogEventRequest =
        new DefaultLogEventRequest(
            null,
            attributeRequests,
            new DefaultTimeRange(Instant.ofEpochMilli(startTime), Instant.ofEpochMilli(endTime)),
            0,
            0,
            List.of(),
            Collections.emptyList());
    LogEventsRequest logEventRequest =
        requestBuilder.buildRequest(defaultLogEventRequest).blockingGet();

    assertEquals(endTime, logEventRequest.getEndTimeMillis());
    assertEquals(startTime, logEventRequest.getStartTimeMillis());
    assertEquals(2, logEventRequest.getSelectionCount());
  }
}
