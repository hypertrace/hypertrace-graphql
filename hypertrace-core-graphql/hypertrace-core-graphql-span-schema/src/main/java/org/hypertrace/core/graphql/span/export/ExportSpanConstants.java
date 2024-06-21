package org.hypertrace.core.graphql.span.export;

import java.util.List;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;

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

    List<AttributeExpression> SPAN_ATTRIBUTES =
        List.of(
            AttributeExpression.forAttributeKey(SpanAttributes.ID),
            AttributeExpression.forAttributeKey(SpanAttributes.SERVICE_NAME),
            AttributeExpression.forAttributeKey(SpanAttributes.TRACE_ID),
            AttributeExpression.forAttributeKey(SpanAttributes.PARENT_SPAN_ID),
            AttributeExpression.forAttributeKey(SpanAttributes.START_TIME),
            AttributeExpression.forAttributeKey(SpanAttributes.END_TIME),
            AttributeExpression.forAttributeKey(SpanAttributes.NAME),
            AttributeExpression.forAttributeKey(SpanAttributes.TAGS));
  }

  interface SpanTagsKey {
    String SERVICE_NAME_KEY = "service.name";
    String SPAN_KIND = "span.kind";
    String STATUS_CODE = "status.code";
    String ERROR = "error";

    List<String> EXCLUDE_KEYS = List.of(SPAN_KIND, STATUS_CODE, ERROR);
    List<String> STATUS_CODE_KEYS = List.of(STATUS_CODE, ERROR);
  }

  interface LogEventAttributes {
    String TIMESTAMP = "timestamp";
    String ATTRIBUTES = "attributes";

    List<AttributeExpression> LOG_EVENT_ATTRIBUTES =
        List.of(
            AttributeExpression.forAttributeKey(LogEventAttributes.TIMESTAMP),
            AttributeExpression.forAttributeKey(LogEventAttributes.ATTRIBUTES));
  }
}
