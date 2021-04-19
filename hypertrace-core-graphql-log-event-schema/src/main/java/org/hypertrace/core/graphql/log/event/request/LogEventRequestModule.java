package org.hypertrace.core.graphql.log.event.request;

import com.google.inject.AbstractModule;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;

public class LogEventRequestModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(LogEventRequestBuilder.class).to(DefaultLogEventRequestBuilder.class);
    requireBinding(ArgumentDeserializer.class);
  }
}
