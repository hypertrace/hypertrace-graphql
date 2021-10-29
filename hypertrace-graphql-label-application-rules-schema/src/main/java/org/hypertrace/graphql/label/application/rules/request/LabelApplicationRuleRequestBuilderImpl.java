package org.hypertrace.graphql.label.application.rules.request;

import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.label.application.rules.deserialization.LabelApplicationRuleIdArgument;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRule;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRuleData;

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
            .deserializeObject(arguments, LabelApplicationRuleData.class)
            .orElseThrow());
  }

  @Override
  public LabelApplicationRuleUpdateRequest buildUpdateLabelApplicationRuleRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments) {
    return new LabelApplicationRuleUpdateRequestImpl(
        requestContext,
        this.argumentDeserializer
            .deserializeObject(arguments, LabelApplicationRule.class)
            .orElseThrow());
  }

  @Override
  public LabelApplicationRuleDeleteRequest buildDeleteLabelApplicationRuleRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments) {
    return new LabelApplicationRuleIdArgumentImpl(
        requestContext,
        this.argumentDeserializer
            .deserializePrimitive(arguments, LabelApplicationRuleIdArgument.class)
            .orElseThrow());
  }

  @Value
  @Accessors(fluent = true)
  private static class LabelApplicationRuleCreateRequestImpl
      implements LabelApplicationRuleCreateRequest {
    GraphQlRequestContext context;
    LabelApplicationRuleData labelApplicationRuleData;
  }

  @Value
  @Accessors(fluent = true)
  private static class LabelApplicationRuleUpdateRequestImpl
      implements LabelApplicationRuleUpdateRequest {
    GraphQlRequestContext context;
    LabelApplicationRule labelApplicationRule;
  }

  @Value
  @Accessors(fluent = true)
  private static class LabelApplicationRuleIdArgumentImpl
      implements LabelApplicationRuleDeleteRequest {
    GraphQlRequestContext context;
    String id;
  }
}
