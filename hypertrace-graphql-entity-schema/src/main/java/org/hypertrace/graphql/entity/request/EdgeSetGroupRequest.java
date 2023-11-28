package org.hypertrace.graphql.entity.request;

import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Map;
import org.hypertrace.core.graphql.common.request.AttributeRequest;

public interface EdgeSetGroupRequest {
  AttributeRequest neighborIdAttribute();

  AttributeRequest neighborTypeAttribute();

  Single<EntityRequest> buildNeighborRequest(String entityType, Collection<String> neighborIds);

  Map<String, EdgeSetRequest> edgeSetRequests();
}
