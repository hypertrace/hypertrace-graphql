package org.hypertrace.core.graphql.common.schema;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.common.deserialization.CommonDeserializationModule;
import org.hypertrace.core.graphql.common.request.CommonRequestModule;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

public class CommonSchemaModule extends AbstractModule {

  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(), GraphQlSchemaFragment.class)
        .addBinding()
        .to(CommonSchemaFragment.class);

    install(new CommonDeserializationModule());
    install(new CommonRequestModule());
  }
}
