package org.hypertrace.core.graphql.log.event.dao;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.log.event.request.LogEventRequest;
import org.hypertrace.core.graphql.log.event.schema.LogEvent;
import org.hypertrace.core.graphql.log.event.schema.LogEventResultSet;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.log.events.LogEventsResponse;

class GatewayServiceLogEventsResponseConverter {

  private final BiConverter<
          Collection<AttributeRequest>, Map<String, Value>, Map<AttributeExpression, Object>>
      attributeMapConverter;

  @Inject
  GatewayServiceLogEventsResponseConverter(
      BiConverter<
              Collection<AttributeRequest>, Map<String, Value>, Map<AttributeExpression, Object>>
          attributeMapConverter) {
    this.attributeMapConverter = attributeMapConverter;
  }

  public Single<LogEventResultSet> convert(LogEventRequest request, LogEventsResponse response) {
    // todo populate total correctly
    return Observable.fromIterable(response.getLogEventsList())
        .concatMapSingle(logEvent -> this.convert(request, logEvent))
        .toList()
        .map(
            logEvents ->
                new ConvertedLogEventResultSet(logEvents, logEvents.size(), logEvents.size()));
  }

  private Single<LogEvent> convert(
      LogEventRequest request, org.hypertrace.gateway.service.v1.log.events.LogEvent logEvent) {
    return this.attributeMapConverter
        .convert(request.attributes(), logEvent.getAttributesMap())
        .map(ConvertedLogEvent::new);
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedLogEvent implements LogEvent {
    Map<AttributeExpression, Object> attributeValues;

    @Override
    public Object attribute(AttributeExpression attributeExpression) {
      return this.attributeValues.get(attributeExpression);
    }
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedLogEventResultSet implements LogEventResultSet {
    List<LogEvent> results;
    long total;
    long count;
  }
}
