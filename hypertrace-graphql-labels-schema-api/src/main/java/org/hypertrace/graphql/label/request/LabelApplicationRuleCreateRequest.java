package org.hypertrace.graphql.label.request;

import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.graphql.label.schema.rule.LabelApplicationRuleData;

public interface LabelApplicationRuleCreateRequest extends ContextualRequest {
  LabelApplicationRuleData labelApplicationRuleData();
}
