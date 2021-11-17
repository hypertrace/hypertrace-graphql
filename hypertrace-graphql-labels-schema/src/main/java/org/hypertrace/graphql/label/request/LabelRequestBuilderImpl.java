package org.hypertrace.graphql.label.request;

import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.label.schema.Label;
import org.hypertrace.graphql.label.schema.mutation.CreateLabel;

public class LabelRequestBuilderImpl implements LabelRequestBuilder {
  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  LabelRequestBuilderImpl(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public LabelCreateRequest buildCreateRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments) {
    return new LabelCreateRequestImpl(
        requestContext,
        this.argumentDeserializer.deserializeObject(arguments, CreateLabel.class).orElseThrow());
  }

  @Override
  public LabelUpdateRequest buildUpdateRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments) {
    return new LabelUpdateRequestImpl(
        requestContext,
        this.argumentDeserializer.deserializeObject(arguments, Label.class).orElseThrow());
  }

  @Value
  @Accessors(fluent = true)
  private static class LabelCreateRequestImpl implements LabelCreateRequest {
    GraphQlRequestContext context;
    CreateLabel label;
  }

  @Value
  @Accessors(fluent = true)
  private static class LabelUpdateRequestImpl implements LabelUpdateRequest {
    GraphQlRequestContext context;
    Label label;
  }
}
