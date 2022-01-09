package org.hypertrace.graphql.metric.request;

import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.stream.Stream;
import org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface MetricAggregationRequestBuilder {
  Observable<MetricAggregationRequest> build(
      AttributeAssociation<AttributeExpression> attributeExpressionAssociation,
      SelectedField metricAggregationContainerField);

  Single<List<MetricAggregationRequest>> build(
      GraphQlRequestContext context,
      String requestScope,
      Stream<SelectedField> metricQueryableFieldStream);

  MetricAggregationRequest build(
      AttributeAssociation<AttributeExpression> attributeExpressionAssociation,
      AttributeModelMetricAggregationType aggregationType,
      List<Object> arguments);
}
