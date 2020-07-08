package org.hypertrace.core.graphql.trace;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;
import org.hypertrace.core.graphql.trace.dao.TraceDaoModule;
import org.hypertrace.core.graphql.trace.deserialization.TraceDeserializationModule;
import org.hypertrace.core.graphql.trace.request.TraceRequestModule;

public class TraceSchemaModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(), GraphQlSchemaFragment.class)
        .addBinding()
        .to(TraceSchemaFragment.class);

    install(new TraceRequestModule());
    install(new TraceDeserializationModule());
    install(new TraceDaoModule());
  }
}
