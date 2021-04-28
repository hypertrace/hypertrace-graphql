package org.hypertrace.core.graphql.span;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.common.request.ResultSetRequestBuilder;
import org.hypertrace.core.graphql.span.dao.SpanDaoModule;
import org.hypertrace.core.graphql.span.request.SpanRequestModule;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

public class SpanSchemaModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(), GraphQlSchemaFragment.class)
        .addBinding()
        .to(SpanSchemaFragment.class);

    requireBinding(ResultSetRequestBuilder.class);
    install(new SpanDaoModule());
    install(new SpanRequestModule());
  }
}
