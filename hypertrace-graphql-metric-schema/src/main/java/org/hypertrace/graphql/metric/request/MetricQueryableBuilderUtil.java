package org.hypertrace.graphql.metric.request;

import static org.hypertrace.graphql.metric.schema.MetricAggregationQueryable.METRIC_FIELD_NAME;

import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeAssociator;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;
import org.hypertrace.graphql.metric.schema.argument.MetricKeyArgument;

class MetricQueryableBuilderUtil {

  private final GraphQlSelectionFinder selectionFinder;
  private final ArgumentDeserializer argumentDeserializer;
  private final AttributeAssociator attributeAssociator;

  @Inject
  MetricQueryableBuilderUtil(
      GraphQlSelectionFinder selectionFinder,
      ArgumentDeserializer argumentDeserializer,
      AttributeAssociator attributeAssociator) {
    this.selectionFinder = selectionFinder;
    this.argumentDeserializer = argumentDeserializer;
    this.attributeAssociator = attributeAssociator;
  }

  <T> Single<List<T>> buildForEachMetricQueryable(
      GraphQlRequestContext context,
      String scope,
      Stream<SelectedField> metricQueryableFieldStream,
      BiFunction<AttributeAssociation<AttributeExpression>, SelectedField, Observable<T>> builder) {

    return Observable.fromStream(metricQueryableFieldStream)
        .flatMap(field -> this.buildForEachMetricContainer(context, scope, field, builder))
        .collect(Collectors.toList());
  }

  private <T> Observable<T> buildForEachMetricContainer(
      GraphQlRequestContext context,
      String scope,
      SelectedField metricQueryableField,
      BiFunction<AttributeAssociation<AttributeExpression>, SelectedField, Observable<T>> builder) {

    return Observable.fromStream(
            this.selectionFinder.findSelections(
                metricQueryableField.getSelectionSet(),
                SelectionQuery.namedChild(METRIC_FIELD_NAME)))
        .flatMap(
            field ->
                this.buildForEachMetricContainer(
                    context, scope, field, attribute -> builder.apply(attribute, field)));
  }

  private <T> Observable<T> buildForEachMetricContainer(
      GraphQlRequestContext context,
      String scope,
      SelectedField metricContainerField,
      Function<AttributeAssociation<AttributeExpression>, Observable<T>> builder) {
    return this.resolveAttributeExpression(context, scope, metricContainerField)
        .flatMapObservable(builder::apply);
  }

  private Maybe<AttributeAssociation<AttributeExpression>> resolveAttributeExpression(
      GraphQlRequestContext context, String scope, SelectedField metricContainerField) {
    Optional<AttributeExpression> maybeExpression =
        this.argumentDeserializer
            .deserializeObject(metricContainerField.getArguments(), AttributeExpression.class)
            .or(
                () ->
                    this.argumentDeserializer
                        .deserializePrimitive(
                            metricContainerField.getArguments(), MetricKeyArgument.class)
                        .map(AttributeExpression::forAttributeKey));

    return Maybe.fromOptional(maybeExpression)
        .flatMapSingle(
            expression ->
                this.attributeAssociator.associateAttribute(
                    context, scope, expression, expression.key()));
  }
}
