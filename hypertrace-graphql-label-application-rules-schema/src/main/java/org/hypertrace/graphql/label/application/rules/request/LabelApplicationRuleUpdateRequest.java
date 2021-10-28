package org.hypertrace.graphql.label.application.rules.request;

import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.graphql.label.application.rules.schema.mutation.UpdateLabelApplicationRule;

public interface LabelApplicationRuleUpdateRequest extends ContextualRequest {
  UpdateLabelApplicationRule updateLabelApplicationRuleRequest();
}
