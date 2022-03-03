package org.hypertrace.graphql.spanprocessing.request.mutation;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ApiNamingRuleCreate;

public class DefaultApiNamingCreateRuleRequestBuilder implements ApiNamingCreateRuleRequestBuilder {

  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  DefaultApiNamingCreateRuleRequestBuilder(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public Single<ApiNamingCreateRuleRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments) {
    ApiNamingRuleCreate apiNamingRuleCreateInput =
        this.argumentDeserializer
            .deserializeObject(arguments, ApiNamingRuleCreate.class)
            .orElseThrow();
    return Single.just(new DefaultApiNamingCreateRuleRequest(context, apiNamingRuleCreateInput));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultApiNamingCreateRuleRequest implements ApiNamingCreateRuleRequest {
    GraphQlRequestContext context;
    ApiNamingRuleCreate createInput;
  }
}
