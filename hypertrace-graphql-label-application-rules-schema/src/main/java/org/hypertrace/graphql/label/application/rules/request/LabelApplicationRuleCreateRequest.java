package org.hypertrace.graphql.label.application.rules.request;

import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRuleData;

public interface LabelApplicationRuleCreateRequest extends ContextualRequest {
  LabelApplicationRuleData labelApplicationRuleData();
}
