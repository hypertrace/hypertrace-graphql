package org.hypertrace.graphql.spanprocessing.request.mutation;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.spanprocessing.schema.mutation.SamplingConfigUpdate;

public class DefaultSamplingConfigUpdateRequestBuilder
    implements SamplingConfigUpdateRequestBuilder {
  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  DefaultSamplingConfigUpdateRequestBuilder(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public Single<SamplingConfigUpdateRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments) {
    SamplingConfigUpdate samplingConfigUpdateInput =
        this.argumentDeserializer
            .deserializeObject(arguments, SamplingConfigUpdate.class)
            .orElseThrow();
    return Single.just(new DefaultSamplingConfigUpdateRequest(context, samplingConfigUpdateInput));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultSamplingConfigUpdateRequest implements SamplingConfigUpdateRequest {
    GraphQlRequestContext context;
    SamplingConfigUpdate updateInput;
  }
}
