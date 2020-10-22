package org.hypertrace.core.graphql.common.request;

import com.google.inject.AbstractModule;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeAssociator;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;

public class CommonRequestModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ResultSetRequestBuilder.class).to(DefaultResultSetRequestBuilder.class);
    bind(AttributeRequestBuilder.class).to(DefaultAttributeRequestBuilder.class);
    bind(FilterRequestBuilder.class).to(DefaultFilterRequestBuilder.class);
    requireBinding(AttributeStore.class);
    requireBinding(ArgumentDeserializer.class);
    requireBinding(AttributeAssociator.class);
    requireBinding(GraphQlSelectionFinder.class);
  }
}
