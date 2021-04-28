package org.hypertrace.core.graphql.span.request;

import static org.hypertrace.core.graphql.common.schema.results.ResultSet.RESULT_SET_RESULTS_NAME;
import static org.hypertrace.core.graphql.span.schema.Span.LOG_EVENT_KEY;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.hypertrace.core.graphql.atttributes.scopes.HypertraceCoreAttributeScopeString;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;

class LogEventAttributeRequestBuilder {

  private final GraphQlSelectionFinder selectionFinder;
  private final AttributeRequestBuilder attributeRequestBuilder;

  @Inject
  LogEventAttributeRequestBuilder(
      GraphQlSelectionFinder selectionFinder, AttributeRequestBuilder attributeRequestBuilder) {
    this.selectionFinder = selectionFinder;
    this.attributeRequestBuilder = attributeRequestBuilder;
  }

  Single<Set<AttributeRequest>> buildAttributeRequest(
      GraphQlRequestContext context, DataFetchingFieldSelectionSet selectionSet) {
    return attributeRequestBuilder
        .buildForAttributeQueryableFields(
            context,
            HypertraceCoreAttributeScopeString.LOG_EVENT,
            getLogEventSelectionFields(selectionSet))
        .collect(Collectors.toUnmodifiableSet());
  }

  private Stream<SelectedField> getLogEventSelectionFields(
      DataFetchingFieldSelectionSet selectionSet) {
    return this.selectionFinder.findSelections(
        selectionSet,
        SelectionQuery.builder()
            .selectionPath(List.of(RESULT_SET_RESULTS_NAME, LOG_EVENT_KEY, RESULT_SET_RESULTS_NAME))
            .build());
  }
}
