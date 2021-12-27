package org.hypertrace.graphql.label.request;

import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.graphql.label.schema.rule.LabelApplicationRule;

public interface LabelApplicationRuleUpdateRequest extends ContextualRequest {
  LabelApplicationRule labelApplicationRule();
}
