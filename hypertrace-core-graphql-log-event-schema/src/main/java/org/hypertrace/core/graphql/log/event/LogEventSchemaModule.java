package org.hypertrace.core.graphql.log.event;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.log.event.dao.LogEventDaoModule;
import org.hypertrace.core.graphql.log.event.request.LogEventRequestModule;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

public class LogEventSchemaModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(), GraphQlSchemaFragment.class)
        .addBinding()
        .to(LogEventSchemaFragment.class);

    install(new LogEventRequestModule());
    install(new LogEventDaoModule());
  }
}
