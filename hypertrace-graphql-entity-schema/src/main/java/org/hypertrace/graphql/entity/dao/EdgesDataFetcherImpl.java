package org.hypertrace.graphql.entity.dao;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.entity.schema.EdgeResultSet;
import org.hypertrace.graphql.entity.schema.Entity;
import org.hypertrace.graphql.entity.schema.EntityType;
import org.hypertrace.graphql.entity.schema.argument.NeighborEntityScopeArgument;
import org.hypertrace.graphql.entity.schema.argument.NeighborEntityTypeArgument;

abstract class EdgesDataFetcherImpl implements DataFetcher<CompletableFuture<EdgeResultSet>> {

  private final ArgumentDeserializer argumentDeserializer;

  protected abstract EdgeResultSet getEdges(
      Entity entity, EntityType neighborType, String neighborScope, List<FilterArgument> filterBy);

  EdgesDataFetcherImpl(ArgumentDeserializer argumentDeserializer) {
    this.argumentDeserializer = argumentDeserializer;
  }

  @Override
  public CompletableFuture<EdgeResultSet> get(DataFetchingEnvironment environment) {
    // TODO: Edges when used with filters have two limitations for now
    // 1. We can only have one entityType incoming/outgoing edge
    // 2. If different filters for same entity type, filters will be merged into one AND filter.
    //
    // We are passing the empty filter as argument to query the edges.
    // This will work for now because we have restricted multiple edges selection in a request
    // Fix this when we will solve the above two limitations.
    return CompletableFuture.completedFuture(
        getEdges(
            environment.getSource(),
            getNeighborEntityType(environment.getArguments()).orElse(null),
            getNeighborEntityScope(environment.getArguments()).orElse(null),
            Collections.emptyList()));
  }

  private Optional<String> getNeighborEntityScope(Map<String, Object> arguments) {
    return this.argumentDeserializer.deserializePrimitive(
        arguments, NeighborEntityScopeArgument.class);
  }

  private Optional<EntityType> getNeighborEntityType(Map<String, Object> arguments) {
    return this.argumentDeserializer.deserializePrimitive(
        arguments, NeighborEntityTypeArgument.class);
  }
}
