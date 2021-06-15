package org.hypertrace.core.graphql.span.dao;

import static io.reactivex.rxjava3.core.Single.zip;

import com.google.common.collect.ImmutableSet;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.atttributes.scopes.HypertraceCoreAttributeScopeString;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.request.FilterRequestBuilder;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterOperatorType;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterType;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.span.request.SpanRequest;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.Filter;
import org.hypertrace.gateway.service.v1.log.events.LogEventsRequest;
import org.hypertrace.gateway.service.v1.span.SpansResponse;

class SpanLogEventRequestBuilder {
  private static final int LOG_EVENT_TOTAL_LIMIT = 1000;

  private final Converter<Collection<AttributeRequest>, Set<Expression>> attributeConverter;
  private final Converter<Collection<AttributeAssociation<FilterArgument>>, Filter> filterConverter;
  private final FilterRequestBuilder filterRequestBuilder;
  private final AttributeStore attributeStore;
  private final AttributeRequestBuilder attributeRequestBuilder;

  @Inject
  SpanLogEventRequestBuilder(
      Converter<Collection<AttributeRequest>, Set<Expression>> attributeConverter,
      Converter<Collection<AttributeAssociation<FilterArgument>>, Filter> filterConverter,
      FilterRequestBuilder filterRequestBuilder,
      AttributeStore attributeStore,
      AttributeRequestBuilder attributeRequestBuilder) {
    this.attributeConverter = attributeConverter;
    this.filterConverter = filterConverter;
    this.filterRequestBuilder = filterRequestBuilder;
    this.attributeStore = attributeStore;
    this.attributeRequestBuilder = attributeRequestBuilder;
  }

  Single<LogEventsRequest> buildLogEventsRequest(
      SpanRequest gqlRequest, SpansResponse spansResponse) {
    return zip(
        getRequestAttributes(
            gqlRequest.spanEventsRequest().context(), gqlRequest.logEventAttributes()),
        buildLogEventsQueryFilter(gqlRequest, spansResponse).flatMap(filterConverter::convert),
        (selections, filter) ->
            LogEventsRequest.newBuilder()
                .setStartTimeMillis(
                    gqlRequest.spanEventsRequest().timeRange().startTime().toEpochMilli())
                .setEndTimeMillis(
                    gqlRequest.spanEventsRequest().timeRange().endTime().toEpochMilli())
                .addAllSelection(selections)
                .setLimit(LOG_EVENT_TOTAL_LIMIT)
                .setFilter(filter)
                .build());
  }

  private Single<Set<Expression>> getRequestAttributes(
      GraphQlRequestContext requestContext, Collection<AttributeRequest> logEventAttributes) {
    return this.attributeStore
        .getForeignIdAttribute(
            requestContext,
            HypertraceCoreAttributeScopeString.LOG_EVENT,
            HypertraceCoreAttributeScopeString.SPAN)
        .map(attributeRequestBuilder::buildForAttribute)
        .toObservable()
        .concatWith(Observable.fromIterable(logEventAttributes))
        .collect(ImmutableSet.toImmutableSet())
        .flatMap(attributeConverter::convert);
  }

  private Single<List<AttributeAssociation<FilterArgument>>> buildLogEventsQueryFilter(
      SpanRequest gqlRequest, SpansResponse spansResponse) {
    List<String> spanIds =
        spansResponse.getSpansList().stream()
            .map(
                spanEvent ->
                    spanEvent
                        .getAttributesMap()
                        .get(gqlRequest.spanEventsRequest().idAttribute().attribute().id())
                        .getString())
            .collect(Collectors.toList());

    return filterRequestBuilder.build(
        gqlRequest.spanEventsRequest().context(),
        HypertraceCoreAttributeScopeString.LOG_EVENT,
        Set.of(new LogEventFilter(spanIds)));
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class LogEventFilter implements FilterArgument {

    FilterType type = FilterType.ID;
    String key = null;
    FilterOperatorType operator = FilterOperatorType.IN;
    Collection<String> value;
    AttributeScope idType = null;
    String idScope = HypertraceCoreAttributeScopeString.SPAN;
  }
}
