package org.hypertrace.core.graphql.schema.registry;

import java.util.Set;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

public interface GraphQlSchemaRegistry {

  Set<GraphQlSchemaFragment> getRegisteredFragments();

  GraphQlSchemaFragment getRootFragment();
}
