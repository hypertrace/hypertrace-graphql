package org.hypertrace.graphql.spanprocessing.request.mutation;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.spanprocessing.schema.mutation.IncludeSpanRuleCreate;

public class DefaultIncludeSpanCreateRuleRequestBuilder
    implements IncludeSpanCreateRuleRequestBuilder {

  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  DefaultIncludeSpanCreateRuleRequestBuilder(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public Single<IncludeSpanCreateRuleRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments) {
    IncludeSpanRuleCreate includeSpanRuleCreateInput =
        this.argumentDeserializer
            .deserializeObject(arguments, IncludeSpanRuleCreate.class)
            .orElseThrow();
    return Single.just(
        new DefaultIncludeSpanCreateRuleRequest(context, includeSpanRuleCreateInput));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultIncludeSpanCreateRuleRequest implements IncludeSpanCreateRuleRequest {
    GraphQlRequestContext context;
    IncludeSpanRuleCreate createInput;
  }
}
