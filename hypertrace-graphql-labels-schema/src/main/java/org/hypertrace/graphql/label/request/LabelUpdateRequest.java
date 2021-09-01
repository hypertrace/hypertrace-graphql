package org.hypertrace.graphql.label.request;

import graphql.GraphQLContext;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.label.schema.Label;

public interface LabelUpdateRequest {
    GraphQlRequestContext context();
    Label newLabel();
}
