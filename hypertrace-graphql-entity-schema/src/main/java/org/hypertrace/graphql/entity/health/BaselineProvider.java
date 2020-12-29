package org.hypertrace.graphql.entity.health;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.gateway.service.v1.baseline.BaselineEntitiesRequest;
import org.hypertrace.gateway.service.v1.baseline.BaselineEntitiesResponse;
import org.hypertrace.gateway.service.v1.entity.EntitiesRequest;
import org.hypertrace.gateway.service.v1.entity.EntitiesResponse;
import org.hypertrace.graphql.entity.request.EntityRequest;

public interface BaselineProvider {
    Single<BaselineEntitiesRequest> buildRequest(EntitiesRequest entitiesRequest, EntitiesResponse entitiesResponse, EntityRequest request);
    Single<BaselineEntitiesResponse> makeRequest(GraphQlRequestContext context, BaselineEntitiesRequest request);

}
