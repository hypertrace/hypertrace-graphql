package org.hypertrace.graphql.label.request;

import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.graphql.label.schema.Label;

public interface LabelUpdateRequest extends ContextualRequest {
  Label label();
}
