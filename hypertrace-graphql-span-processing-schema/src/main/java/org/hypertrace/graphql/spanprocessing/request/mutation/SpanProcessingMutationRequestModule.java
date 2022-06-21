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

    bind(IncludeSpanCreateRuleRequestBuilder.class)
        .to(DefaultIncludeSpanCreateRuleRequestBuilder.class);
    bind(IncludeSpanUpdateRuleRequestBuilder.class)
        .to(DefaultIncludeSpanUpdateRuleRequestBuilder.class);
    bind(IncludeSpanDeleteRuleRequestBuilder.class)
        .to(DefaultIncludeSpanDeleteRuleRequestBuilder.class);

    bind(ApiNamingCreateRuleRequestBuilder.class)
        .to(DefaultApiNamingCreateRuleRequestBuilder.class);
    bind(ApiNamingUpdateRuleRequestBuilder.class)
        .to(DefaultApiNamingUpdateRuleRequestBuilder.class);
    bind(ApiNamingDeleteRuleRequestBuilder.class)
        .to(DefaultApiNamingDeleteRuleRequestBuilder.class);

    requireBinding(ArgumentDeserializer.class);
  }
}
