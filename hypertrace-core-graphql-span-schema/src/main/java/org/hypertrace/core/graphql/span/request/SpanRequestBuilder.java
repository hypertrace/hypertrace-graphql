package org.hypertrace.core.graphql.span.request;

import graphql.schema.DataFetchingFieldSelectionSet;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Map;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface SpanRequestBuilder {

  Single<SpanRequest> build(
      GraphQlRequestContext context,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet);

  Single<SpanRequest> build(
      GraphQlRequestContext context,
      Map<String, Object> arguments,
      List<String> spanAttributes,
      List<String> logAttributes);
}
