package org.hypertrace.core.graphql.common.request;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface ResultSetRequestBuilder {
  Single<ResultSetRequest<OrderArgument>> build(
      GraphQlRequestContext context,
      String requestScope,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet);

  <O extends OrderArgument> Single<ResultSetRequest<O>> build(
      GraphQlRequestContext context,
      String requestScope,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet,
      Class<O> orderArgumentClass);

  <O extends OrderArgument> Single<ResultSetRequest<O>> build(
      GraphQlRequestContext context,
      String requestScope,
      int limit,
      int offset,
      TimeRangeArgument timeRange,
      List<AttributeAssociation<O>> orderArguments,
      Collection<AttributeAssociation<FilterArgument>> filterArguments,
      Stream<SelectedField> attributeQueryableFields,
      Optional<String> spaceId);

  <O extends OrderArgument> Single<ResultSetRequest<O>> rebuildWithAdditionalFilters(
      ResultSetRequest<O> originalRequest, Collection<FilterArgument> additionalFilters);

  Single<ResultSetRequest<OrderArgument>> build(
      GraphQlRequestContext context,
      String requestScope,
      Map<String, Object> arguments,
      List<AttributeExpression> attributeExpressions);
}
