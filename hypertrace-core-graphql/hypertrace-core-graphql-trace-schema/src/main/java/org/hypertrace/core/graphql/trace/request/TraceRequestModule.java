package org.hypertrace.core.graphql.trace.request;

import com.google.inject.AbstractModule;
import org.hypertrace.core.graphql.common.request.ResultSetRequestBuilder;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;

public class TraceRequestModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TraceRequestBuilder.class).to(DefaultTraceRequestBuilder.class);

    requireBinding(ArgumentDeserializer.class);
    requireBinding(ResultSetRequestBuilder.class);
  }
}
