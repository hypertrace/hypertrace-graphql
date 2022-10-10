package org.hypertrace.graphql.spanprocessing.request.mutation;

import com.google.inject.AbstractModule;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;

public class SpanProcessingMutationRequestModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ExcludeSpanCreateRuleRequestBuilder.class)
        .to(DefaultExcludeSpanCreateRuleRequestBuilder.class);
    bind(ExcludeSpanUpdateRuleRequestBuilder.class)
        .to(DefaultExcludeSpanUpdateRuleRequestBuilder.class);
    bind(ExcludeSpanDeleteRuleRequestBuilder.class)
        .to(DefaultExcludeSpanDeleteRuleRequestBuilder.class);

    requireBinding(ArgumentDeserializer.class);
  }
}
