package org.hypertrace.graphql.label.application.rules.request;

import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.label.application.rules.schema.mutation.CreateLabelApplicationRule;
import org.hypertrace.graphql.label.application.rules.schema.mutation.UpdateLabelApplicationRule;

public class LabelApplicationRuleRequestBuilderImpl implements LabelApplicationRuleRequestBuilder {
  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  public LabelApplicationRuleRequestBuilderImpl(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public LabelApplicationRuleCreateRequest buildCreateLabelApplicationRuleRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments) {
    return new LabelApplicationRuleCreateRequestImpl(
        requestContext,
        this.argumentDeserializer
            .deserializeObject(arguments, CreateLabelApplicationRule.class)
            .orElseThrow());
  }

  @Override
  public LabelApplicationRuleUpdateRequest buildUpdateLabelApplicationRuleRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments) {
    return new LabelApplicationRuleUpdateRequestImpl(
        requestContext,
        this.argumentDeserializer
            .deserializeObject(arguments, UpdateLabelApplicationRule.class)
            .orElseThrow());
  }

  @Value
  @Accessors(fluent = true)
  private static class LabelApplicationRuleCreateRequestImpl
      implements LabelApplicationRuleCreateRequest {
    GraphQlRequestContext context;
    CreateLabelApplicationRule createLabelApplicationRuleRequest;
  }

  @Value
  @Accessors(fluent = true)
  private static class LabelApplicationRuleUpdateRequestImpl
      implements LabelApplicationRuleUpdateRequest {
    GraphQlRequestContext context;
    UpdateLabelApplicationRule updateLabelApplicationRuleRequest;
  }
}
