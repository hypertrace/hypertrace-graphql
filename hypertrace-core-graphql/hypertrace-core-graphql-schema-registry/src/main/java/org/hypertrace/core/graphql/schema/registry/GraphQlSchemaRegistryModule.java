package org.hypertrace.core.graphql.schema.registry;

import com.google.inject.AbstractModule;
import graphql.schema.GraphQLSchema;

public class GraphQlSchemaRegistryModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(GraphQlSchemaRegistry.class).to(DefaultGraphQlSchemaRegistry.class);
    bind(GraphQLSchema.class).toProvider(GraphQlAnnotatedSchemaMerger.class);
  }
}
