package org.hypertrace.graphql.label.request;

import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface LabelDeleteRequest {
    GraphQlRequestContext context();
    String id();
}
