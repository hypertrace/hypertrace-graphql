package org.hypertrace.graphql.spanprocessing.request.mutation;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.spanprocessing.schema.mutation.IncludeSpanRuleDelete;

public class DefaultIncludeSpanDeleteRuleRequestBuilder
    implements IncludeSpanDeleteRuleRequestBuilder {

  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  DefaultIncludeSpanDeleteRuleRequestBuilder(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public Single<IncludeSpanDeleteRuleRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments) {
    String idToDelete =
        this.argumentDeserializer
            .deserializeObject(arguments, IncludeSpanRuleDelete.class)
            .map(IncludeSpanRuleDelete::id)
            .orElseThrow();
    return Single.just(new DefaultIncludeSpanDeleteRuleRequest(context, idToDelete));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultIncludeSpanDeleteRuleRequest implements IncludeSpanDeleteRuleRequest {
    GraphQlRequestContext context;
    String id;
  }
}
