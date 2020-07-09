package org.hypertrace.graphql.entity.request;

import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Set;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.graphql.entity.schema.EntityType;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;

public interface EdgeSetGroupRequest {

  Set<EntityType> entityTypes();

  // Includes neighbor id and type
  Collection<AttributeRequest> attributeRequests();

  Collection<MetricAggregationRequest> metricAggregationRequests();

  AttributeRequest neighborIdAttribute();

  AttributeRequest neighborTypeAttribute();

  Single<EntityRequest> buildNeighborRequest(EntityType entityType, Collection<String> neighborIds);
}
