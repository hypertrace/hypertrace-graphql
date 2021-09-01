package org.hypertrace.graphql.label.request;

import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeScopeStringTranslator;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.label.deserialization.LabelIdArgument;
import org.hypertrace.graphql.label.deserialization.LabelKeyArgument;
import org.hypertrace.graphql.label.schema.Label;

public class LabelsConfigRequestBuilderImpl implements LabelsConfigRequestBuilder {
  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  LabelsConfigRequestBuilderImpl(
      ArgumentDeserializer argumentDeserializer,
      AttributeScopeStringTranslator scopeStringTranslator) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public LabelCreateRequest buildCreateRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments) {
    return new LabelCreateRequestImpl(
        requestContext,
        this.argumentDeserializer
            .deserializePrimitive(arguments, LabelKeyArgument.class)
            .orElseThrow());
  }

  @Value
  @Accessors(fluent = true)
  private static class LabelCreateRequestImpl implements LabelCreateRequest {
    GraphQlRequestContext context;
    String key;
  }

  @Override
  public LabelDeleteRequest buildDeleteRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments) {
    return new LabelDeleteRequestImpl(
        requestContext,
        this.argumentDeserializer
            .deserializePrimitive(arguments, LabelIdArgument.class)
            .orElseThrow());
  }

  @Value
  @Accessors(fluent = true)
  private static class LabelDeleteRequestImpl implements LabelDeleteRequest {
    GraphQlRequestContext context;
    String id;
  }

  @Override
  public LabelUpdateRequest buildUpdateRequest(
      GraphQlRequestContext requestContext, Map<String, Object> arguments) {
    System.out.println(arguments);
    return new LabelUpdateRequestImpl(
        requestContext,
        this.argumentDeserializer.deserializeObject(arguments, Label.class).orElseThrow());
  }

  @Value
  @Accessors(fluent = true)
  private static class LabelUpdateRequestImpl implements LabelUpdateRequest {
    GraphQlRequestContext context;
    Label newLabel;
  }
}
