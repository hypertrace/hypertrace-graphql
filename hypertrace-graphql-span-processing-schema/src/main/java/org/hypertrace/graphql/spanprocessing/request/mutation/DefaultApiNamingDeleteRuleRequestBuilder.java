package org.hypertrace.graphql.spanprocessing.request.mutation;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ApiNamingRuleDelete;

public class DefaultApiNamingDeleteRuleRequestBuilder implements ApiNamingDeleteRuleRequestBuilder {

  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  DefaultApiNamingDeleteRuleRequestBuilder(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public Single<ApiNamingDeleteRuleRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments) {
    String idToDelete =
        this.argumentDeserializer
            .deserializeObject(arguments, ApiNamingRuleDelete.class)
            .map(ApiNamingRuleDelete::id)
            .orElseThrow();
    return Single.just(new DefaultApiNamingDeleteRuleRequest(context, idToDelete));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultApiNamingDeleteRuleRequest implements ApiNamingDeleteRuleRequest {
    GraphQlRequestContext context;
    String id;
  }
}
