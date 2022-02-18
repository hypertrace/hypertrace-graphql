package org.hypertrace.graphql.spanprocessing.request.mutation;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ExcludeSpanRuleDelete;

public class DefaultExcludeSpanDeleteRuleRequestBuilder
    implements ExcludeSpanDeleteRuleRequestBuilder {

  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  DefaultExcludeSpanDeleteRuleRequestBuilder(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public Single<ExcludeSpanDeleteRuleRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments) {
    String idToDelete =
        this.argumentDeserializer
            .deserializeObject(arguments, ExcludeSpanRuleDelete.class)
            .map(ExcludeSpanRuleDelete::id)
            .orElseThrow();
    return Single.just(new DefaultExcludeSpanDeleteRuleRequest(context, idToDelete));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultExcludeSpanDeleteRuleRequest implements ExcludeSpanDeleteRuleRequest {
    GraphQlRequestContext context;
    String id;
  }
}
