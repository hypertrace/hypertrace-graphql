package org.hypertrace.graphql.label.request;

import com.google.inject.AbstractModule;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeScopeStringTranslator;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;

public class LabelRequestModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(LabelRequestBuilder.class).to(LabelRequestBuilderImpl.class);

    requireBinding(ArgumentDeserializer.class);
    requireBinding(AttributeScopeStringTranslator.class);
  }
}
