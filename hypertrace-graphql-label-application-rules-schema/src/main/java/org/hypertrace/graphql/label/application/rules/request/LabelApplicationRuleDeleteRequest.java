package org.hypertrace.graphql.label.application.rules.request;

import org.hypertrace.core.graphql.common.request.ContextualRequest;

public interface LabelApplicationRuleDeleteRequest extends ContextualRequest {
  String id();
}
