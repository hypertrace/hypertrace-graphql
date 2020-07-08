package org.hypertrace.core.graphql.span.dao;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.span.schema.Span;
import org.hypertrace.core.graphql.span.schema.SpanResultSet;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.span.SpanEvent;
import org.hypertrace.gateway.service.v1.span.SpansResponse;

class GatewayServiceSpanConverter {

  private final BiConverter<Collection<AttributeRequest>, Map<String, Value>, Map<String, Object>>
      attributeMapConverter;

  @Inject
  GatewayServiceSpanConverter(
      BiConverter<Collection<AttributeRequest>, Map<String, Value>, Map<String, Object>>
          attributeMapConverter) {
    this.attributeMapConverter = attributeMapConverter;
  }

  public Single<SpanResultSet> convert(ResultSetRequest<?> request, SpansResponse response) {
    int total = response.getTotal();

    return Observable.fromIterable(response.getSpansList())
        .flatMapSingle(spanEvent -> this.convert(request, spanEvent))
        .toList()
        .map(spans -> new ConvertedSpanResultSet(spans, total, spans.size()));
  }

  private Single<Span> convert(ResultSetRequest<?> request, SpanEvent spanEvent) {
    return this.attributeMapConverter
        .convert(request.attributes(), spanEvent.getAttributesMap())
        .map(
            attrMap ->
                new ConvertedSpan(
                    attrMap.get(request.idAttribute().attribute().key()).toString(), attrMap));
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedSpan implements Span {
    String id;
    Map<String, Object> attributeValues;

    @Override
    public Object attribute(String key) {
      return this.attributeValues.get(key);
    }
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedSpanResultSet implements SpanResultSet {
    List<Span> results;
    long total;
    long count;
  }
}
