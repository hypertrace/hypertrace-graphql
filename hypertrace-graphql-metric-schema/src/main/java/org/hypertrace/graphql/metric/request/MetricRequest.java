package org.hypertrace.graphql.metric.request;

import java.util.List;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;

public interface MetricRequest {
  AttributeAssociation<AttributeExpression> attributeExpressionAssociation();

  List<MetricAggregationRequest> aggregationRequests();

  List<MetricSeriesRequest> seriesRequests();

  List<MetricSeriesRequest> baselineSeriesRequests();
}
