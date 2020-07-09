package org.hypertrace.core.graphql.common.request;

import static io.reactivex.rxjava3.core.Single.zip;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.LimitArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.OffsetArgument;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeAssociator;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;

class DefaultResultSetRequestBuilder implements ResultSetRequestBuilder {
  private final ArgumentDeserializer argumentDeserializer;
  private final GraphQlSelectionFinder selectionFinder;
  private final AttributeRequestBuilder attributeRequestBuilder;
  private final AttributeAssociator attributeAssociator;
  private final FilterRequestBuilder filterRequestBuilder;
  private static final int DEFAULT_LIMIT = 100;
  private static final int DEFAULT_OFFSET = 0;

  @Inject
  DefaultResultSetRequestBuilder(
      ArgumentDeserializer argumentDeserializer,
      GraphQlSelectionFinder selectionFinder,
      AttributeRequestBuilder attributeRequestBuilder,
      AttributeAssociator attributeAssociator,
      FilterRequestBuilder filterRequestBuilder) {
    this.argumentDeserializer = argumentDeserializer;
    this.selectionFinder = selectionFinder;
    this.attributeRequestBuilder = attributeRequestBuilder;
    this.attributeAssociator = attributeAssociator;
    this.filterRequestBuilder = filterRequestBuilder;
  }

  @Override
  public Single<ResultSetRequest<OrderArgument>> build(
      GraphQlRequestContext context,
      AttributeModelScope requestScope,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet) {
    return this.build(context, requestScope, arguments, selectionSet, OrderArgument.class);
  }

  @Override
  public <O extends OrderArgument> Single<ResultSetRequest<O>> build(
      GraphQlRequestContext context,
      AttributeModelScope requestScope,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet,
      Class<O> orderArgumentClass) {
    int limit =
        this.argumentDeserializer
            .deserializePrimitive(arguments, LimitArgument.class)
            .orElse(DEFAULT_LIMIT);

    int offset =
        this.argumentDeserializer
            .deserializePrimitive(arguments, OffsetArgument.class)
            .orElse(DEFAULT_OFFSET);

    TimeRangeArgument timeRange =
        this.argumentDeserializer
            .deserializeObject(arguments, TimeRangeArgument.class)
            .orElseThrow();

    List<O> requestedOrders =
        this.argumentDeserializer
            .deserializeObjectList(arguments, orderArgumentClass)
            .orElse(Collections.emptyList());

    List<FilterArgument> requestedFilters =
        this.argumentDeserializer
            .deserializeObjectList(arguments, FilterArgument.class)
            .orElse(Collections.emptyList());

    return zip(
            this.attributeAssociator
                .associateAttributes(context, requestScope, requestedOrders, OrderArgument::key)
                .collect(Collectors.toUnmodifiableList()),
            this.filterRequestBuilder.build(context, requestScope, requestedFilters),
            (orders, filters) ->
                this.build(
                    context,
                    requestScope,
                    limit,
                    offset,
                    timeRange,
                    orders,
                    filters,
                    this.getAttributeQueryableFields(selectionSet)))
        .flatMap(single -> single);
  }

  @Override
  public <O extends OrderArgument> Single<ResultSetRequest<O>> build(
      GraphQlRequestContext context,
      AttributeModelScope requestScope,
      int limit,
      int offset,
      TimeRangeArgument timeRange,
      List<AttributeAssociation<O>> orderArguments,
      Collection<AttributeAssociation<FilterArgument>> filterArguments,
      Stream<SelectedField> attributeQueryableFields) {
    return zip(
        this.attributeRequestBuilder
            .buildForAttributeQueryableFieldsAndId(context, requestScope, attributeQueryableFields)
            .collect(Collectors.toUnmodifiableSet()),
        this.attributeRequestBuilder.buildForId(context, requestScope),
        (attributeRequests, idAttribute) ->
            new DefaultResultSetRequest<>(
                context,
                attributeRequests,
                idAttribute,
                timeRange,
                limit,
                offset,
                orderArguments,
                filterArguments));
  }

  private Stream<SelectedField> getAttributeQueryableFields(
      DataFetchingFieldSelectionSet selectionSet) {
    return this.selectionFinder.findSelections(
        selectionSet, SelectionQuery.namedChild(ResultSet.RESULT_SET_RESULTS_NAME));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultResultSetRequest<O extends OrderArgument>
      implements ResultSetRequest<O> {
    GraphQlRequestContext context;
    Collection<AttributeRequest> attributes;
    AttributeRequest idAttribute;
    TimeRangeArgument timeRange;
    int limit;
    int offset;
    List<AttributeAssociation<O>> orderArguments;
    Collection<AttributeAssociation<FilterArgument>> filterArguments;
  }
}
