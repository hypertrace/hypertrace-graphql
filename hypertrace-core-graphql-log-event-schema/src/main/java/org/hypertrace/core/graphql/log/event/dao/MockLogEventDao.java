package org.hypertrace.core.graphql.log.event.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Map;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.log.event.request.LogEventRequest;
import org.hypertrace.core.graphql.log.event.schema.LogEvent;
import org.hypertrace.core.graphql.log.event.schema.LogEventResultSet;

public class MockLogEventDao implements LogEventDao {

  @Override
  public Single<LogEventResultSet> getLogEvents(LogEventRequest request) {
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, String> attributes =
        Map.of(
            "event", "Acquired lock with 0 transaction waiting",
            "network", "tcp",
            "message", "Slow transaction");
    String attributeString;
    try {
      attributeString = objectMapper.writeValueAsString(attributes);
    } catch (JsonProcessingException e) {
      attributeString = "";
    }

    LogEvent logEvent1 =
        new MockLogEvent(
            Map.of(
                "spanId",
                "span-1",
                "traceId",
                "trace-1",
                "timestamp",
                System.nanoTime(),
                "attributes",
                attributeString));

    LogEvent logEvent2 =
        new MockLogEvent(
            Map.of(
                "spanId",
                "span-2",
                "traceId",
                "trace-1",
                "timestamp",
                System.nanoTime(),
                "attributes",
                attributeString));
    LogEventResultSet logEventResultSet =
        new MockLogEventResultSet(List.of(logEvent1, logEvent2), 2, 2);

    return Single.just(logEventResultSet);
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class MockLogEvent implements LogEvent {
    Map<String, Object> attributeValues;

    @Override
    public Object attribute(String key) {
      return this.attributeValues.get(key);
    }
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class MockLogEventResultSet implements LogEventResultSet {
    List<LogEvent> results;
    long total;
    long count;
  }
}
