package org.hypertrace.graphql.entity.request;

import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Map;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;

public interface EdgeSetGroupRequest {
  AttributeRequest neighborIdAttribute();

  AttributeRequest neighborTypeAttribute();

  Single<EntityRequest> buildNeighborRequest(String entityType, Collection<String> neighborIds);

  Map<String, EdgeSetRequest> edgeSetRequests();

  Collection<AttributeRequest> getAllAttributeRequests();

  Collection<MetricAggregationRequest> getAllMetricAggregationRequests();
}
