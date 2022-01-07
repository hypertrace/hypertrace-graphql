package org.hypertrace.core.graphql.span.request;

import graphql.schema.DataFetchingFieldSelectionSet;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Map;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface SpanRequestBuilder {

  Single<SpanRequest> build(
      GraphQlRequestContext context,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet);

  Single<SpanRequest> build(
      GraphQlRequestContext context,
      Map<String, Object> arguments,
      List<AttributeExpression> spanAttributeExpressions,
      List<AttributeExpression> logAttributeExpressions);
}
