package org.hypertrace.core.graphql.log.event.request;

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
import org.hypertrace.core.graphql.atttributes.scopes.HypertraceCoreAttributeScopeString;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.request.FilterRequestBuilder;
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

public class DefaultLogEventRequestBuilder implements LogEventRequestBuilder {

  private final ArgumentDeserializer argumentDeserializer;
  private final GraphQlSelectionFinder selectionFinder;
  private final AttributeRequestBuilder attributeRequestBuilder;
  private final AttributeAssociator attributeAssociator;
  private final FilterRequestBuilder filterRequestBuilder;
  private static final int DEFAULT_LIMIT = 100;
  private static final int DEFAULT_OFFSET = 0;

  @Inject
  DefaultLogEventRequestBuilder(
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
  public Single<LogEventRequest> build(
      GraphQlRequestContext context,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet) {
    return this.build(
        context,
        HypertraceCoreAttributeScopeString.LOG_EVENT,
        arguments,
        selectionSet,
        OrderArgument.class);
  }

  Single<LogEventRequest> build(
      GraphQlRequestContext context,
      String requestScope,
      Map<String, Object> arguments,
      DataFetchingFieldSelectionSet selectionSet,
      Class<OrderArgument> orderArgumentClass) {
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

    List<OrderArgument> requestedOrders =
        this.argumentDeserializer
            .deserializeObjectList(arguments, orderArgumentClass)
            .orElse(Collections.emptyList());

    List<FilterArgument> requestedFilters =
        this.argumentDeserializer
            .deserializeObjectList(arguments, FilterArgument.class)
            .orElse(Collections.emptyList());

    return zip(
            this.attributeRequestBuilder
                .buildForAttributeQueryableFields(
                    context, requestScope, getAttributeQueryableFields(selectionSet))
                .collect(Collectors.toUnmodifiableSet()),
            this.attributeAssociator
                .associateAttributes(
                    context,
                    requestScope,
                    requestedOrders,
                    arg -> arg.resolvedKeyExpression().key())
                .collect(Collectors.toUnmodifiableList()),
            this.filterRequestBuilder.build(context, requestScope, requestedFilters),
            (attributeRequests, orders, filters) ->
                Single.just(
                    new DefaultLogEventRequest(
                        context, attributeRequests, timeRange, limit, offset, orders, filters)))
        .flatMap(single -> single);
  }

  private Stream<SelectedField> getAttributeQueryableFields(
      DataFetchingFieldSelectionSet selectionSet) {
    return this.selectionFinder.findSelections(
        selectionSet, SelectionQuery.namedChild(ResultSet.RESULT_SET_RESULTS_NAME));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLogEventRequest implements LogEventRequest {

    GraphQlRequestContext context;
    Collection<AttributeRequest> attributes;
    TimeRangeArgument timeRange;
    int limit;
    int offset;
    List<AttributeAssociation<OrderArgument>> orderArguments;
    Collection<AttributeAssociation<FilterArgument>> filterArguments;
  }
}
