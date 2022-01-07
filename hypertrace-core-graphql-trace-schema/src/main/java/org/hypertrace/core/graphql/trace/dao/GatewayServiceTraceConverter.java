package org.hypertrace.core.graphql.trace.dao;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.trace.schema.Trace;
import org.hypertrace.core.graphql.trace.schema.TraceResultSet;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.trace.TracesResponse;

public class GatewayServiceTraceConverter {
  private final BiConverter<
          Collection<AttributeRequest>, Map<String, Value>, Map<AttributeExpression, Object>>
      attributeMapConverter;

  @Inject
  GatewayServiceTraceConverter(
      BiConverter<
              Collection<AttributeRequest>, Map<String, Value>, Map<AttributeExpression, Object>>
          attributeMapConverter) {
    this.attributeMapConverter = attributeMapConverter;
  }

  public Single<TraceResultSet> convert(ResultSetRequest<?> request, TracesResponse response) {
    int total = response.getTotal();

    return Observable.fromIterable(response.getTracesList())
        .flatMapSingle(trace -> this.convert(request, trace))
        .toList()
        .map(traces -> new ConvertedTraceResultSet(traces, total, traces.size()));
  }

  private Single<Trace> convert(
      ResultSetRequest<?> request, org.hypertrace.gateway.service.v1.trace.Trace trace) {
    return this.attributeMapConverter
        .convert(request.attributes(), trace.getAttributesMap())
        .map(
            attrMap ->
                new ConvertedTrace(
                    attrMap.get(request.idAttribute().attributeExpression().value()).toString(),
                    attrMap));
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedTrace implements Trace {
    String id;
    Map<AttributeExpression, Object> attributeValues;

    @Override
    public Object attribute(AttributeExpression attributeExpression) {
      return this.attributeValues.get(attributeExpression);
    }
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedTraceResultSet implements TraceResultSet {
    List<Trace> results;
    long total;
    long count;
  }
}
