package org.hypertrace.graphql.metric.request;

import java.util.List;
import org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;

public interface MetricAggregationRequest {

  AttributeAssociation<AttributeExpression> attributeExpression();

  AttributeModelMetricAggregationType aggregation();

  String alias();

  List<Object> arguments();

  boolean baseline();
}
