package org.hypertrace.graphql.entity.request;

import java.util.Collection;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;

public interface EdgeSetRequest {
  // list of filter arguments
  Collection<AttributeAssociation<FilterArgument>> filterArguments();

  // Includes neighbor id and type
  Collection<AttributeRequest> attributeRequests();

  Collection<MetricAggregationRequest> metricAggregationRequests();
}
