package org.hypertrace.graphql.label.request;

import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.graphql.label.schema.mutation.UpdateLabel;

public interface LabelUpdateRequest extends ContextualRequest {
  UpdateLabel label();
}
