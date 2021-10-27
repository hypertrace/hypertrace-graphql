package org.hypertrace.graphql.label.application.rules.request;

import com.google.inject.AbstractModule;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;

public class LabelApplicationRuleRequestModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(LabelApplicationRuleRequestBuilder.class).to(LabelApplicationRuleRequestBuilderImpl.class);
    requireBinding(ArgumentDeserializer.class);
  }
}
