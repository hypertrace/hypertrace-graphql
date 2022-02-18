package org.hypertrace.graphql.spanprocessing.request.mutation;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ExcludeSpanRuleCreate;

public class DefaultExcludeSpanCreateRuleRequestBuilder
    implements ExcludeSpanCreateRuleRequestBuilder {

  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  DefaultExcludeSpanCreateRuleRequestBuilder(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public Single<ExcludeSpanCreateRuleRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments) {
    ExcludeSpanRuleCreate excludeSpanRuleCreateInput =
        this.argumentDeserializer
            .deserializeObject(arguments, ExcludeSpanRuleCreate.class)
            .orElseThrow();
    return Single.just(
        new DefaultExcludeSpanCreateRuleRequest(context, excludeSpanRuleCreateInput));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultExcludeSpanCreateRuleRequest implements ExcludeSpanCreateRuleRequest {
    GraphQlRequestContext context;
    ExcludeSpanRuleCreate createInput;
  }
}
