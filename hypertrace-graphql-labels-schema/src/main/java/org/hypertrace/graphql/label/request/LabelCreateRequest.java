package org.hypertrace.graphql.label.request;

import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.label.schema.mutation.CreateLabel;

public interface LabelCreateRequest {
  GraphQlRequestContext context();

  CreateLabel label();
}
