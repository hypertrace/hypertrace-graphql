package org.hypertrace.core.graphql.span.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType;
import org.hypertrace.core.graphql.attributes.AttributeModelType;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterOperatorType;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterType;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.span.request.SpanRequest;
import org.hypertrace.gateway.service.v1.common.ValueType;
import org.hypertrace.gateway.service.v1.log.events.LogEvent;
import org.hypertrace.gateway.service.v1.log.events.LogEventsResponse;
import org.hypertrace.gateway.service.v1.span.SpanEvent;
import org.hypertrace.gateway.service.v1.span.SpansResponse;

class DaoTestUtil {

  @Value
  @Accessors(fluent = true)
  static class DefaultAttributeRequest implements AttributeRequest {

    AttributeModel attribute;

    @Override
    public String alias() {
      return attribute.id();
    }
  }

  @Value
  @Accessors(fluent = true)
  static class DefaultAttributeModel implements AttributeModel {

    String id;
    String scope;
    String key;
    String displayName;
    AttributeModelType type;
    String units;
    boolean onlySupportsGrouping;
    boolean onlySupportsAggregation;
    List<AttributeModelMetricAggregationType> supportedMetricAggregationTypes;
    boolean groupable;
  }

  @Value
  @Accessors(fluent = true)
  static class NormalizedFilter implements FilterArgument {

    FilterType type = FilterType.ATTRIBUTE;
    String key;
    FilterOperatorType operator;
    Object value;
    String idScope = null;
    AttributeScope idType = null;
  }

  @Value
  @Accessors(fluent = true)
  static class DefaultSpanRequest implements SpanRequest {
    GraphQlRequestContext context;
    ResultSetRequest<OrderArgument> spanEventsRequest;
    Collection<AttributeRequest> logEventAttributes;
  }

  @Value
  @Accessors(fluent = true)
  static class DefaultResultSetRequest implements ResultSetRequest<OrderArgument> {

    GraphQlRequestContext context;
    Collection<AttributeRequest> attributes;
    TimeRangeArgument timeRange;
    AttributeRequest idAttribute;
    int limit;
    int offset;
    List<AttributeAssociation<OrderArgument>> orderArguments;
    Collection<AttributeAssociation<FilterArgument>> filterArguments;
    Optional<String> spaceId;
  }

  @Value
  @Accessors(fluent = true)
  static class DefaultTimeRange implements TimeRangeArgument {

    @JsonProperty(TIME_RANGE_ARGUMENT_START_TIME)
    Instant startTime;

    @JsonProperty(TIME_RANGE_ARGUMENT_END_TIME)
    Instant endTime;
  }

  static AttributeRequest traceIdAttribute =
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
              false));

  static AttributeRequest spanIdAttribute =
      new DefaultAttributeRequest(
          new DefaultAttributeModel(
              "spanId",
              "LOG_EVENT",
              "spanId",
              "Span Id",
              AttributeModelType.STRING,
              "",
              false,
              false,
              Collections.emptyList(),
              false));

  static AttributeRequest attributesAttribute =
      new DefaultAttributeRequest(
          new DefaultAttributeModel(
              "attributes",
              "LOG_EVENT",
              "attributes",
              "Attributes",
              AttributeModelType.STRING,
              "",
              false,
              false,
              Collections.emptyList(),
              false));

  static AttributeRequest eventIdAttribute =
      new DefaultAttributeRequest(
          new DefaultAttributeModel(
              "id",
              "EVENT",
              "id",
              "Id",
              AttributeModelType.STRING,
              "",
              false,
              false,
              Collections.emptyList(),
              false));

  static SpansResponse spansResponse =
      SpansResponse.newBuilder()
          .addSpans(
              SpanEvent.newBuilder()
                  .putAllAttributes(Map.of("id", getValue("span1"), "traceId", getValue("trace1")))
                  .build())
          .addSpans(
              SpanEvent.newBuilder()
                  .putAllAttributes(Map.of("id", getValue("span2"), "traceId", getValue("trace1")))
                  .build())
          .addSpans(
              SpanEvent.newBuilder()
                  .putAllAttributes(Map.of("id", getValue("span3"), "traceId", getValue("trace1")))
                  .build())
          .build();

  static LogEventsResponse logEventsResponse =
      LogEventsResponse.newBuilder()
          .addLogEvents(
              LogEvent.newBuilder()
                  .putAllAttributes(
                      Map.of(
                          "traceId",
                          getValue("trace1"),
                          "attributes",
                          getValue("event: error"),
                          "spanId",
                          getValue("span1"))))
          .addLogEvents(
              LogEvent.newBuilder()
                  .putAllAttributes(
                      Map.of(
                          "traceId",
                          getValue("trace1"),
                          "attributes",
                          getValue("event: error"),
                          "spanId",
                          getValue("span1"))))
          .addLogEvents(
              LogEvent.newBuilder()
                  .putAllAttributes(
                      Map.of(
                          "traceId",
                          getValue("trace1"),
                          "attributes",
                          getValue("event: error"),
                          "spanId",
                          getValue("span2"))))
          .build();

  static org.hypertrace.gateway.service.v1.common.Value getValue(String value) {
    return org.hypertrace.gateway.service.v1.common.Value.newBuilder()
        .setValueType(ValueType.STRING)
        .setString(value)
        .build();
  }
}
