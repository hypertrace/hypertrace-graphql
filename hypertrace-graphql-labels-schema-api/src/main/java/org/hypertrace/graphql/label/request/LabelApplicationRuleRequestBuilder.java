package org.hypertrace.graphql.label.request;

import java.util.Map;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface LabelApplicationRuleRequestBuilder {
  LabelApplicationRuleCreateRequest buildCreateLabelApplicationRuleRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments);

  LabelApplicationRuleUpdateRequest buildUpdateLabelApplicationRuleRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments);

  LabelApplicationRuleDeleteRequest buildDeleteLabelApplicationRuleRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments);
}
