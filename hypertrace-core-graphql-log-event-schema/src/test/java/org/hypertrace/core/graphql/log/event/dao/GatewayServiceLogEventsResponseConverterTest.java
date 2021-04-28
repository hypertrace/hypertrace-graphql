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
import java.util.Map;
import org.hypertrace.core.graphql.attributes.AttributeModelType;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.log.event.schema.LogEventResultSet;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.gateway.GatewayUtilsModule;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.common.ValueType;
import org.hypertrace.gateway.service.v1.log.events.LogEvent;
import org.hypertrace.gateway.service.v1.log.events.LogEventsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GatewayServiceLogEventsResponseConverterTest extends BaseDaoTest {

  private GatewayServiceLogEventsResponseConverter responseConverter;

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
    BiConverter<Collection<AttributeRequest>, Map<String, Value>, Map<String, Object>>
        attributeMapConverter =
            injector.getInstance(
                Key.get(
                    new TypeLiteral<
                        BiConverter<
                            Collection<AttributeRequest>,
                            Map<String, Value>,
                            Map<String, Object>>>() {}));
    responseConverter = new GatewayServiceLogEventsResponseConverter(attributeMapConverter);
  }

  @Test
  void testConvert() {
    long startTime = System.currentTimeMillis();
    long endTime = System.currentTimeMillis() + Duration.ofHours(1).toMillis();
    LogEventsResponse logEventsResponse =
        LogEventsResponse.newBuilder()
            .addLogEvents(
                LogEvent.newBuilder()
                    .putAllAttributes(
                        Map.of(
                            "traceId",
                                Value.newBuilder()
                                    .setString("trace1")
                                    .setValueType(ValueType.STRING)
                                    .build(),
                            "timestamp",
                                Value.newBuilder()
                                    .setValueType(ValueType.TIMESTAMP)
                                    .setTimestamp(Duration.ofMillis(startTime).toNanos())
                                    .build())))
            .build();
    Collection<AttributeRequest> attributeRequests =
        List.of(
            new DefaultAttributeRequest(
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
                    false)),
            new DefaultAttributeRequest(
                new DefaultAttributeModel(
                    "timestamp",
                    "LOG_EVENT",
                    "timestamp",
                    "Timestamp",
                    AttributeModelType.TIMESTAMP,
                    "ns",
                    false,
                    false,
                    Collections.emptyList(),
                    false)));
    DefaultLogEventRequest defaultLogEventRequest =
        new DefaultLogEventRequest(
            null,
            attributeRequests,
            new DefaultTimeRange(Instant.ofEpochMilli(startTime), Instant.ofEpochMilli(endTime)),
            0,
            0,
            List.of(),
            Collections.emptyList());
    LogEventResultSet logEventResultSet =
        responseConverter.convert(defaultLogEventRequest, logEventsResponse).blockingGet();
    assertEquals(1, logEventResultSet.results().size());
    assertEquals("trace1", logEventResultSet.results().get(0).attribute("traceId"));
    assertEquals(
        Instant.ofEpochSecond(0, Duration.ofMillis(startTime).toNanos()),
        logEventResultSet.results().get(0).attribute("timestamp"));
  }
}
