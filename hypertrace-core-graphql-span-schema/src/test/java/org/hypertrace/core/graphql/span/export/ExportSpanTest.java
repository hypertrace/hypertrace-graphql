package org.hypertrace.core.graphql.span.export;

import com.google.common.io.BaseEncoding;
import com.google.protobuf.ByteString;
import io.opentelemetry.proto.trace.v1.Span.SpanKind;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.log.event.schema.LogEvent;
import org.hypertrace.core.graphql.log.event.schema.LogEventResultSet;
import org.hypertrace.core.graphql.span.export.ExportSpan.Builder;
import org.hypertrace.core.graphql.span.export.ExportSpanConstants.LogEventAttributes;
import org.hypertrace.core.graphql.span.export.ExportSpanConstants.SpanAttributes;
import org.hypertrace.core.graphql.span.schema.Span;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExportSpanTest {

  @lombok.Value
  @Accessors(fluent = true)
  static class ConvertedSpan implements Span {
    String id;
    Map<String, Object> attributeValues;
    Map<String, List<LogEvent>> spanIdToLogEvents;

    @Override
    public Object attribute(String key) {
      return this.attributeValues.get(key);
    }

    @Override
    public LogEventResultSet logEvents() {
      List<LogEvent> list = spanIdToLogEvents.getOrDefault(id, Collections.emptyList());
      return new ConvertedLogEventResultSet(list, list.size(), list.size());
    }
  }

  @lombok.Value
  @Accessors(fluent = true)
  static class ConvertedLogEventResultSet implements LogEventResultSet {
    List<LogEvent> results;
    long total;
    long count;
  }

  @lombok.Value
  @Accessors(fluent = true)
  static class ConvertedLogEvent implements org.hypertrace.core.graphql.log.event.schema.LogEvent {

    Map<String, Object> attributeValues;

    @Override
    public Object attribute(String key) {
      return this.attributeValues.get(key);
    }
  }

  @Test
  public void testIncludeAllFields() {
    Span span =
        new ConvertedSpan(
            "40c4edcea5a17fea",
            Map.of(
                SpanAttributes.ID, "40c4edcea5a17fea",
                SpanAttributes.PARENT_SPAN_ID, "4dfeb104f6ed73bf",
                SpanAttributes.TRACE_ID, "00000000000000004dfeb104f6ed73bf",
                SpanAttributes.SERVICE_NAME, "frontend",
                SpanAttributes.NAME, "HTTP GET: /customer",
                SpanAttributes.START_TIME, 1620743557759L,
                SpanAttributes.END_TIME, 1620743558040L,
                SpanAttributes.TAGS,
                    Map.of(
                        "http.status_code", "200",
                        "component", "net/http",
                        "span.kind", "server",
                        "sampler.type", "const",
                        "sampler.param", "true",
                        "http.url", "/dispatch?customer=123&nonse=0.04728538603377741",
                        "servicename", "frontend",
                        "http.method", "GET",
                        "status.code", "0")),
            Map.of());

    ExportSpan exportSpan = new Builder(span).build();

    // check resource
    Assertions.assertEquals(1, exportSpan.resourceSpans().getResource().getAttributesCount());

    Assertions.assertEquals(
        "service.name", exportSpan.resourceSpans().getResource().getAttributes(0).getKey());
    Assertions.assertEquals(
        "frontend",
        exportSpan.resourceSpans().getResource().getAttributes(0).getValue().getStringValue());

    // check for span count, start time, end time
    Assertions.assertEquals(1, exportSpan.resourceSpans().getInstrumentationLibrarySpansCount());

    Assertions.assertEquals(
        1620743557759L * 1_000_000L,
        exportSpan
            .resourceSpans()
            .getInstrumentationLibrarySpans(0)
            .getSpans(0)
            .getStartTimeUnixNano());

    Assertions.assertEquals(
        1620743558040L * 1_000_000L,
        exportSpan
            .resourceSpans()
            .getInstrumentationLibrarySpans(0)
            .getSpans(0)
            .getEndTimeUnixNano());

    // two attributes are excluded
    Assertions.assertEquals(
        7,
        exportSpan
            .resourceSpans()
            .getInstrumentationLibrarySpans(0)
            .getSpans(0)
            .getAttributesCount());

    // test for span.kind
    Assertions.assertEquals(
        SpanKind.SPAN_KIND_SERVER,
        exportSpan.resourceSpans().getInstrumentationLibrarySpans(0).getSpans(0).getKind());

    // test IDs
    Assertions.assertEquals(
        ByteString.copyFrom(BaseEncoding.base64().decode("40c4edcea5a17fea")),
        exportSpan.resourceSpans().getInstrumentationLibrarySpans(0).getSpans(0).getSpanId());

    Assertions.assertEquals(
        ByteString.copyFrom(BaseEncoding.base64().decode("4dfeb104f6ed73bf")),
        exportSpan.resourceSpans().getInstrumentationLibrarySpans(0).getSpans(0).getParentSpanId());

    Assertions.assertEquals(
        ByteString.copyFrom(BaseEncoding.base64().decode("00000000000000004dfeb104f6ed73bf")),
        exportSpan.resourceSpans().getInstrumentationLibrarySpans(0).getSpans(0).getTraceId());
  }

  @Test
  public void testWithMissingFields() {
    Span span =
        new ConvertedSpan(
            "40c4edcea5a17fea",
            Map.of(
                SpanAttributes.ID,
                "40c4edcea5a17fea",
                SpanAttributes.TRACE_ID,
                "00000000000000004dfeb104f6ed73bf",
                SpanAttributes.START_TIME,
                1620743557759L,
                SpanAttributes.END_TIME,
                1620743558040L),
            Map.of());

    ExportSpan exportSpan = new Builder(span).build();

    // check resource
    Assertions.assertEquals(0, exportSpan.resourceSpans().getResource().getAttributesCount());

    // check for span count, start time, end time
    Assertions.assertEquals(1, exportSpan.resourceSpans().getInstrumentationLibrarySpansCount());

    Assertions.assertEquals(
        1620743557759L * 1_000_000L,
        exportSpan
            .resourceSpans()
            .getInstrumentationLibrarySpans(0)
            .getSpans(0)
            .getStartTimeUnixNano());

    Assertions.assertEquals(
        1620743558040L * 1_000_000L,
        exportSpan
            .resourceSpans()
            .getInstrumentationLibrarySpans(0)
            .getSpans(0)
            .getEndTimeUnixNano());

    // two attributes are excluded
    Assertions.assertEquals(
        0,
        exportSpan
            .resourceSpans()
            .getInstrumentationLibrarySpans(0)
            .getSpans(0)
            .getAttributesCount());

    // test for span.kind
    Assertions.assertEquals(
        SpanKind.SPAN_KIND_UNSPECIFIED,
        exportSpan.resourceSpans().getInstrumentationLibrarySpans(0).getSpans(0).getKind());

    // test IDs
    Assertions.assertEquals(
        ByteString.copyFrom(BaseEncoding.base64().decode("40c4edcea5a17fea")),
        exportSpan.resourceSpans().getInstrumentationLibrarySpans(0).getSpans(0).getSpanId());

    Assertions.assertEquals(
        ByteString.copyFrom(BaseEncoding.base64().decode("")),
        exportSpan.resourceSpans().getInstrumentationLibrarySpans(0).getSpans(0).getParentSpanId());

    Assertions.assertEquals(
        ByteString.copyFrom(BaseEncoding.base64().decode("00000000000000004dfeb104f6ed73bf")),
        exportSpan.resourceSpans().getInstrumentationLibrarySpans(0).getSpans(0).getTraceId());
  }

  @Test
  public void testForLogEventsFields() {
    List<LogEvent> logEventsUnderTestSpanId =
        List.of(
            new ConvertedLogEvent(
                Map.of(
                    LogEventAttributes.TIMESTAMP,
                    "2021-05-25T16:26:00.310958Z",
                    LogEventAttributes.ATTRIBUTES,
                    Map.of("level", "warn", "message", "redis timeout"))));

    List<LogEvent> logEvents =
        List.of(
            new ConvertedLogEvent(
                Map.of(
                    LogEventAttributes.TIMESTAMP,
                    "2021-05-25T14:57:57.187Z",
                    LogEventAttributes.ATTRIBUTES,
                    Map.of("level", "info", "message", "request successful"))));

    Span span =
        new ConvertedSpan(
            "40c4edcea5a17fea",
            Map.of(
                SpanAttributes.ID,
                "40c4edcea5a17fea",
                SpanAttributes.PARENT_SPAN_ID,
                "4dfeb104f6ed73bf",
                SpanAttributes.TRACE_ID,
                "00000000000000004dfeb104f6ed73bf",
                SpanAttributes.START_TIME,
                1620743557759L,
                SpanAttributes.END_TIME,
                1620743558040L),
            Map.of(
                "40c4edcea5a17fea", logEventsUnderTestSpanId,
                "40c4edcea5a17feb", logEvents));

    ExportSpan exportSpan = new Builder(span).build();

    // check for logs
    Assertions.assertEquals(
        1,
        exportSpan.resourceSpans().getInstrumentationLibrarySpans(0).getSpans(0).getEventsCount());

    Assertions.assertEquals(
        1621959960310958000L,
        exportSpan
            .resourceSpans()
            .getInstrumentationLibrarySpans(0)
            .getSpans(0)
            .getEvents(0)
            .getTimeUnixNano());

    Assertions.assertEquals(
        2,
        exportSpan
            .resourceSpans()
            .getInstrumentationLibrarySpans(0)
            .getSpans(0)
            .getEvents(0)
            .getAttributesCount());

    Assertions.assertTrue(
        exportSpan
            .resourceSpans()
            .getInstrumentationLibrarySpans(0)
            .getSpans(0)
            .getEvents(0)
            .getAttributesList()
            .stream()
            .anyMatch(
                attribute ->
                    attribute.getKey().equals("level")
                        && attribute.getValue().getStringValue().equals("warn")));
  }
}
