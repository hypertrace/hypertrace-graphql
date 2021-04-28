package org.hypertrace.core.graphql.span.request;

import com.google.inject.AbstractModule;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.request.FilterRequestBuilder;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeAssociator;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeScopeStringTranslator;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;

public class SpanRequestModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(SpanRequestBuilder.class).to(DefaultSpanRequestBuilder.class);
    requireBinding(ArgumentDeserializer.class);
    requireBinding(AttributeRequestBuilder.class);
    requireBinding(FilterRequestBuilder.class);
    requireBinding(AttributeStore.class);
    requireBinding(AttributeAssociator.class);
    requireBinding(GraphQlSelectionFinder.class);
    requireBinding(AttributeScopeStringTranslator.class);
    requireBinding(AttributeRequestBuilder.class);
  }
}
