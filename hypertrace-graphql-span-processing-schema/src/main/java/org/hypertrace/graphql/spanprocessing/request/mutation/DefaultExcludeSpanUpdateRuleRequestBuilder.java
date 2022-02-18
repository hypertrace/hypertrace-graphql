package org.hypertrace.graphql.spanprocessing.request.mutation;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ExcludeSpanRuleUpdate;

public class DefaultExcludeSpanUpdateRuleRequestBuilder
    implements ExcludeSpanUpdateRuleRequestBuilder {

  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  DefaultExcludeSpanUpdateRuleRequestBuilder(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public Single<ExcludeSpanUpdateRuleRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments) {
    ExcludeSpanRuleUpdate excludeSpanRuleUpdateInput =
        this.argumentDeserializer
            .deserializeObject(arguments, ExcludeSpanRuleUpdate.class)
            .orElseThrow();
    return Single.just(
        new DefaultExcludeSpanUpdateRuleRequest(context, excludeSpanRuleUpdateInput));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultExcludeSpanUpdateRuleRequest implements ExcludeSpanUpdateRuleRequest {
    GraphQlRequestContext context;
    ExcludeSpanRuleUpdate updateInput;
  }
}
