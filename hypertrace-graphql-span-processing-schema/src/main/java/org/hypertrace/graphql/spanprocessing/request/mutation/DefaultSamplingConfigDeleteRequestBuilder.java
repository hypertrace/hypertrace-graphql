package org.hypertrace.graphql.spanprocessing.request.mutation;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.spanprocessing.schema.mutation.SamplingConfigDelete;

public class DefaultSamplingConfigDeleteRequestBuilder
    implements SamplingConfigDeleteRequestBuilder {

  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  DefaultSamplingConfigDeleteRequestBuilder(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public Single<SamplingConfigDeleteRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments) {
    String idToDelete =
        this.argumentDeserializer
            .deserializeObject(arguments, SamplingConfigDelete.class)
            .map(SamplingConfigDelete::id)
            .orElseThrow();
    return Single.just(new DefaultSamplingConfigDeleteRequest(context, idToDelete));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultSamplingConfigDeleteRequest implements SamplingConfigDeleteRequest {
    GraphQlRequestContext context;
    String id;
  }
}
