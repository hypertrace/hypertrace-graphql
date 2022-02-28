package org.hypertrace.graphql.spanprocessing.request.mutation;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ApiNamingRuleUpdate;

public class DefaultApiNamingUpdateRuleRequestBuilder implements ApiNamingUpdateRuleRequestBuilder {

  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  DefaultApiNamingUpdateRuleRequestBuilder(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public Single<ApiNamingUpdateRuleRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments) {
    ApiNamingRuleUpdate ApiNamingRuleUpdateInput =
        this.argumentDeserializer
            .deserializeObject(arguments, ApiNamingRuleUpdate.class)
            .orElseThrow();
    return Single.just(new DefaultApiNamingUpdateRuleRequest(context, ApiNamingRuleUpdateInput));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultApiNamingUpdateRuleRequest implements ApiNamingUpdateRuleRequest {
    GraphQlRequestContext context;
    ApiNamingRuleUpdate updateInput;
  }
}
