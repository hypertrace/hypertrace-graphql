package org.hypertrace.graphql.explorer.request;

import static org.hypertrace.core.graphql.common.schema.results.ResultSet.RESULT_SET_RESULTS_NAME;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.time.TimeUnit;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeAssociator;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;
import org.hypertrace.graphql.explorer.schema.ExploreResult;
import org.hypertrace.graphql.explorer.schema.argument.SelectionAggregationTypeArgument;
import org.hypertrace.graphql.explorer.schema.argument.SelectionKeyArgument;
import org.hypertrace.graphql.explorer.schema.argument.SelectionSizeArgument;
import org.hypertrace.graphql.explorer.schema.argument.SelectionUnitArgument;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.request.MetricAggregationRequestBuilder;
import org.hypertrace.graphql.metric.request.MetricArguments;

class ExploreSelectionRequestBuilder {
  private final GraphQlSelectionFinder selectionFinder;
  private final ArgumentDeserializer argumentDeserializer;
  private final AttributeAssociator attributeAssociator;
  private final Converter<MetricAggregationType, AttributeModelMetricAggregationType>
      aggregationTypeConverter;
  private final AttributeRequestBuilder attributeRequestBuilder;
  private final MetricAggregationRequestBuilder aggregationRequestBuilder;

  @Inject
  ExploreSelectionRequestBuilder(
      GraphQlSelectionFinder selectionFinder,
      ArgumentDeserializer argumentDeserializer,
      AttributeAssociator attributeAssociator,
      Converter<MetricAggregationType, AttributeModelMetricAggregationType>
          aggregationTypeConverter,
      AttributeRequestBuilder attributeRequestBuilder,
      MetricAggregationRequestBuilder aggregationRequestBuilder) {
    this.selectionFinder = selectionFinder;
    this.argumentDeserializer = argumentDeserializer;
    this.attributeAssociator = attributeAssociator;
    this.aggregationTypeConverter = aggregationTypeConverter;
    this.attributeRequestBuilder = attributeRequestBuilder;
    this.aggregationRequestBuilder = aggregationRequestBuilder;
  }

  Single<Set<AttributeRequest>> getAttributeSelections(
      GraphQlRequestContext requestContext,
      String explorerScope,
      DataFetchingFieldSelectionSet exploreSelectionSet) {
    return this.getSelections(exploreSelectionSet, SelectionType.ATTRIBUTE)
        .flatMapSingle(
            arguments ->
                this.attributeRequestBuilder.buildForAttributeExpression(
                    requestContext, explorerScope, arguments.getAttributeExpression()))
        .collect(Collectors.toUnmodifiableSet());
  }

  Single<Set<MetricAggregationRequest>> getAggregationSelections(
      GraphQlRequestContext requestContext,
      String explorerScope,
      DataFetchingFieldSelectionSet exploreSelectionSet) {
    return this.getSelections(exploreSelectionSet, SelectionType.AGGREGATION)
        .flatMapSingle(
            arguments -> this.buildAggregationRequest(requestContext, explorerScope, arguments))
        .collect(Collectors.toUnmodifiableSet());
  }

  private Observable<SelectionArguments> getSelections(
      DataFetchingFieldSelectionSet exploreSelectionSet, SelectionType selectionType) {
    return Observable.fromStream(
            this.selectionFinder.findSelections(
                exploreSelectionSet,
                SelectionQuery.builder()
                    .selectionPath(
                        List.of(
                            RESULT_SET_RESULTS_NAME, ExploreResult.EXPLORE_RESULT_SELECTION_KEY))
                    .build()))
        .map(this::getSelectionArguments)
        .filter(arguments -> arguments.getSelectionType().equals(selectionType));
  }

  private SelectionArguments getSelectionArguments(SelectedField selectedField) {
    AttributeExpression attributeExpression =
        this.argumentDeserializer
            .deserializeObject(selectedField.getArguments(), AttributeExpression.class)
            .or(
                () ->
                    this.argumentDeserializer
                        .deserializePrimitive(
                            selectedField.getArguments(), SelectionKeyArgument.class)
                        .map(AttributeExpression::forAttributeKey))
            .orElseThrow();

    return this.getAggregationType(selectedField)
        .map(
            aggregationType ->
                new SelectionArguments(
                    attributeExpression,
                    aggregationType,
                    this.getArguments(selectedField, aggregationType)))
        .orElse(new SelectionArguments(attributeExpression));
  }

  private Optional<AttributeModelMetricAggregationType> getAggregationType(
      SelectedField selectedField) {
    return this.argumentDeserializer
        .deserializePrimitive(selectedField.getArguments(), SelectionAggregationTypeArgument.class)
        .map(this.aggregationTypeConverter::convert)
        .map(Single::blockingGet)
        .or(Optional::empty);
  }

  private Single<MetricAggregationRequest> buildAggregationRequest(
      GraphQlRequestContext requestContext, String explorerScope, SelectionArguments arguments) {
    return this.attributeAssociator
        .associateAttribute(
            requestContext,
            explorerScope,
            arguments.getAttributeExpression(),
            arguments.getAttributeExpression().key())
        .map(
            attributeExpressionAttributeAssociation ->
                this.aggregationRequestBuilder.build(
                    attributeExpressionAttributeAssociation,
                    arguments.getAggregationType(),
                    arguments.getAggregationArguments()));
  }

  private List<Object> getArguments(
      SelectedField selectedField, AttributeModelMetricAggregationType aggregationType) {
    Optional<ChronoUnit> unit =
        this.argumentDeserializer
            .deserializePrimitive(selectedField.getArguments(), SelectionUnitArgument.class)
            .map(TimeUnit::getChronoUnit);

    Optional<Integer> size =
        this.argumentDeserializer.deserializePrimitive(
            selectedField.getArguments(), SelectionSizeArgument.class);
    switch (aggregationType) {
      case AVGRATE:
        return MetricArguments.avgRateWithPeriod(
            Duration.of(size.orElseThrow(), unit.orElseThrow()));
      case PERCENTILE:
        return MetricArguments.percentileWithSize(size.orElseThrow());
      case DISTINCT_COUNT:
      case DISTINCT:
      case COUNT:
      case AVG:
      case SUM:
      case MIN:
      case MAX:
      default:
        return Collections.emptyList();
    }
  }

  private enum SelectionType {
    ATTRIBUTE,
    AGGREGATION
  }

  @Value
  @AllArgsConstructor
  private static class SelectionArguments {
    @Nonnull SelectionType selectionType;
    @Nonnull AttributeExpression attributeExpression;
    @Nullable AttributeModelMetricAggregationType aggregationType;
    @Nullable List<Object> aggregationArguments;

    SelectionArguments(AttributeExpression attributeExpression) {
      this(SelectionType.ATTRIBUTE, attributeExpression, null, null);
    }

    SelectionArguments(
        AttributeExpression attributeExpression,
        AttributeModelMetricAggregationType aggregationType,
        List<Object> aggregationArguments) {
      this(SelectionType.AGGREGATION, attributeExpression, aggregationType, aggregationArguments);
    }
  }
}
