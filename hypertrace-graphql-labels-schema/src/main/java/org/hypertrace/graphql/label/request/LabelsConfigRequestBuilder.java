package org.hypertrace.graphql.label.request;

import org.hypertrace.core.graphql.context.GraphQlRequestContext;

import java.util.Map;

public interface LabelsConfigRequestBuilder {
    LabelCreateRequest buildCreateRequest(GraphQlRequestContext requestContext, Map<String, Object> arguments);
    LabelDeleteRequest buildDeleteRequest(GraphQlRequestContext requestContext, Map<String, Object> arguments);
    LabelUpdateRequest buildUpdateRequest(GraphQlRequestContext requestContext, Map<String, Object> arguments);
}
