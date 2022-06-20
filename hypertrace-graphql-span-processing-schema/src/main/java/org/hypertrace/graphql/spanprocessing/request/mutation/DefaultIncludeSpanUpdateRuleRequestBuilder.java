package org.hypertrace.graphql.spanprocessing.request.mutation;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.spanprocessing.schema.mutation.IncludeSpanRuleUpdate;

public class DefaultIncludeSpanUpdateRuleRequestBuilder
    implements IncludeSpanUpdateRuleRequestBuilder {

  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  DefaultIncludeSpanUpdateRuleRequestBuilder(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public Single<IncludeSpanUpdateRuleRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments) {
    IncludeSpanRuleUpdate includeSpanRuleUpdateInput =
        this.argumentDeserializer
            .deserializeObject(arguments, IncludeSpanRuleUpdate.class)
            .orElseThrow();
    return Single.just(
        new DefaultIncludeSpanUpdateRuleRequest(context, includeSpanRuleUpdateInput));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultIncludeSpanUpdateRuleRequest implements IncludeSpanUpdateRuleRequest {
    GraphQlRequestContext context;
    IncludeSpanRuleUpdate updateInput;
  }
}
