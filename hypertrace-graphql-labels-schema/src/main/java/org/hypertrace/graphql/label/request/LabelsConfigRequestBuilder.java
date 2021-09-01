package org.hypertrace.graphql.label.request;

import java.util.Map;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface LabelsConfigRequestBuilder {
  LabelCreateRequest buildCreateRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments);

  LabelDeleteRequest buildDeleteRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments);

  LabelUpdateRequest buildUpdateRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments);
}
