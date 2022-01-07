package org.hypertrace.core.graphql.span.dao;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.log.event.schema.LogEvent;
import org.hypertrace.core.graphql.log.event.schema.LogEventResultSet;
import org.hypertrace.core.graphql.span.request.SpanRequest;
import org.hypertrace.core.graphql.span.schema.Span;
import org.hypertrace.core.graphql.span.schema.SpanResultSet;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.span.SpanEvent;

class GatewayServiceSpanConverter {

  private final BiConverter<
          Collection<AttributeRequest>, Map<String, Value>, Map<AttributeExpression, Object>>
      attributeMapConverter;

  @Inject
  GatewayServiceSpanConverter(
      BiConverter<
              Collection<AttributeRequest>, Map<String, Value>, Map<AttributeExpression, Object>>
          attributeMapConverter) {
    this.attributeMapConverter = attributeMapConverter;
  }

  public Single<SpanResultSet> convert(SpanRequest request, SpanLogEventsResponse response) {
    int total = response.spansResponse().getTotal();

    return Observable.fromIterable(response.spansResponse().getSpansList())
        .flatMapSingle(spanEvent -> this.convert(request, spanEvent, response.spanIdToLogEvents()))
        .toList()
        .map(spans -> new ConvertedSpanResultSet(spans, total, spans.size()));
  }

  private Single<Span> convert(
      SpanRequest request, SpanEvent spanEvent, Map<String, List<LogEvent>> spanIdToLogEvents) {
    return this.attributeMapConverter
        .convert(request.spanEventsRequest().attributes(), spanEvent.getAttributesMap())
        .map(
            attrMap ->
                new ConvertedSpan(
                    attrMap
                        .get(
                            request.spanEventsRequest().idAttribute().attributeExpression().value())
                        .toString(),
                    attrMap,
                    spanIdToLogEvents));
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedSpan implements Span {
    String id;
    Map<AttributeExpression, Object> attributeValues;
    Map<String, List<LogEvent>> spanIdToLogEvents;

    @Override
    public Object attribute(AttributeExpression attributeExpression) {
      return this.attributeValues.get(attributeExpression);
    }

    @Override
    public LogEventResultSet logEvents() {
      List<LogEvent> list = spanIdToLogEvents.getOrDefault(id, Collections.emptyList());
      return new ConvertedLogEventResultSet(list, list.size(), list.size());
    }
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedSpanResultSet implements SpanResultSet {
    List<Span> results;
    long total;
    long count;
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedLogEventResultSet implements LogEventResultSet {
    List<LogEvent> results;
    long total;
    long count;
  }
}
