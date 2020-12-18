package org.hypertrace.graphql.entity.health;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.gateway.service.v1.health.BaselineEntityRequest;
import org.hypertrace.gateway.service.v1.health.BaselineEntityResponse;
import org.hypertrace.graphql.entity.request.EntityRequest;

public interface BaselineProvider {
    Single<BaselineEntityRequest> buildRequest(EntityRequest request);
    Single<BaselineEntityResponse> makeRequest(GraphQlRequestContext context, BaselineEntityRequest request);
}
