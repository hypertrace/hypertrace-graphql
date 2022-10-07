package org.hypertrace.core.graphql.schema.registry;

import java.util.Set;
import javax.inject.Inject;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

class DefaultGraphQlSchemaRegistry implements GraphQlSchemaRegistry {

  private final Set<GraphQlSchemaFragment> schemaFragments;
  private final DefaultSchema defaultSchemaFragment;

  @Inject
  DefaultGraphQlSchemaRegistry(
      Set<GraphQlSchemaFragment> schemaFragments, DefaultSchema rootSchemaFragment) {
    this.schemaFragments = Set.copyOf(schemaFragments);
    this.defaultSchemaFragment = rootSchemaFragment;
  }

  @Override
  public Set<GraphQlSchemaFragment> getRegisteredFragments() {
    return this.schemaFragments;
  }

  @Override
  public DefaultSchema getRootFragment() {
    return this.defaultSchemaFragment;
  }
}
