package org.hypertrace.graphql.label.application.rules.request;

import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.label.application.rule.config.service.v1.DeleteLabelApplicationRuleRequest;

public interface LabelApplicationRuleDeleteRequest extends ContextualRequest {
  DeleteLabelApplicationRuleRequest deleteLabelApplicationRuleRequest();
}
