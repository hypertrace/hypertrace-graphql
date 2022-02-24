package org.hypertrace.graphql.spanprocessing;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;
import org.hypertrace.graphql.spanprocessing.dao.SpanProcessingDaoModule;
import org.hypertrace.graphql.spanprocessing.deserialization.SpanProcessingDeserializationModule;
import org.hypertrace.graphql.spanprocessing.request.mutation.SpanProcessingMutationRequestModule;

public class SpanProcessingSchemaModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(), GraphQlSchemaFragment.class)
        .addBinding()
        .to(SpanProcessingSchemaFragment.class);

    install(new SpanProcessingMutationRequestModule());
    install(new SpanProcessingDaoModule());
    install(new SpanProcessingDeserializationModule());
  }
}
