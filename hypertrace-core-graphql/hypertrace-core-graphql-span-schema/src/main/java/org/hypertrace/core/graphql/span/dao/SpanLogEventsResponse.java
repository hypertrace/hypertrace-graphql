package org.hypertrace.core.graphql.span.dao;

import java.util.List;
import java.util.Map;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.log.event.schema.LogEvent;
import org.hypertrace.gateway.service.v1.span.SpansResponse;

@lombok.Value
@Accessors(fluent = true)
class SpanLogEventsResponse {

  SpansResponse spansResponse;
  Map<String, List<LogEvent>> spanIdToLogEvents;
}
