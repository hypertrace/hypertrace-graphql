package org.hypertrace.graphql.spanprocessing.request.mutation;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.spanprocessing.schema.mutation.SamplingConfigCreate;

public class DefaultSamplingConfigCreateRequestBuilder
    implements SamplingConfigCreateRequestBuilder {

  private final ArgumentDeserializer argumentDeserializer;

  @Inject
  DefaultSamplingConfigCreateRequestBuilder(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public Single<SamplingConfigCreateRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments) {
    SamplingConfigCreate samplingConfigCreateInput =
        this.argumentDeserializer
            .deserializeObject(arguments, SamplingConfigCreate.class)
            .orElseThrow();
    return Single.just(new DefaultSamplingConfigCreateRequest(context, samplingConfigCreateInput));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultSamplingConfigCreateRequest implements SamplingConfigCreateRequest {
    GraphQlRequestContext context;
    SamplingConfigCreate createInput;
  }
}
