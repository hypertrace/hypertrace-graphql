package org.hypertrace.core.graphql.span.export;

import java.util.List;

public interface ExportSpanConstants {
  interface SpanAttributes {
    String ID = "id";
    String SERVICE_NAME = "serviceName";
    String TRACE_ID = "traceId";
    String PARENT_SPAN_ID = "parentSpanId";
    String START_TIME = "startTime";
    String END_TIME = "endTime";
    String NAME = "displaySpanName";
    String TAGS = "spanTags";

    static List<String> getSpanAttributes() {
      return List.of(
          SpanAttributes.ID,
          SpanAttributes.SERVICE_NAME,
          SpanAttributes.TRACE_ID,
          SpanAttributes.PARENT_SPAN_ID,
          SpanAttributes.START_TIME,
          SpanAttributes.END_TIME,
          SpanAttributes.NAME,
          SpanAttributes.TAGS);
    }
  }

  interface SpanTagsKey {
    String SERVICE_NAME_KEY = "service.name";
    String SPAN_KIND = "span.kind";
    String STATUS_CODE = "status.code";
    String ERROR = "error";

    static List<String> getExcludeKeys() {
      return List.of(SPAN_KIND, STATUS_CODE, ERROR);
    }

    static List<String> getStatusCodeKeys() {
      return List.of(STATUS_CODE, ERROR);
    }
  }
}
