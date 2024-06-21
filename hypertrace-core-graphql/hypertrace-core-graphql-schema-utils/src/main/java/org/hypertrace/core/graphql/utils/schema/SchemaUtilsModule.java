package org.hypertrace.core.graphql.utils.schema;

import com.google.inject.AbstractModule;

public class SchemaUtilsModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(GraphQlSelectionFinder.class).to(DefaultGraphQlSelectionFinder.class);
  }
}
