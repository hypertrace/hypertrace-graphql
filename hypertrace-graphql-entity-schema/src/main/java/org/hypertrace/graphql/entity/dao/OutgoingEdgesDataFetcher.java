package org.hypertrace.graphql.entity.dao;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.entity.schema.EdgeResultSet;
import org.hypertrace.graphql.entity.schema.Entity;
import org.hypertrace.graphql.entity.schema.EntityType;
import org.hypertrace.graphql.entity.schema.argument.NeighborEntityScopeArgument;
import org.hypertrace.graphql.entity.schema.argument.NeighborEntityTypeArgument;

public class OutgoingEdgesDataFetcher extends InjectableDataFetcher<EdgeResultSet> {
  public OutgoingEdgesDataFetcher() {
    super(OutgoingEdgesDataFetcherImpl.class);
  }

  private static class OutgoingEdgesDataFetcherImpl
      implements DataFetcher<CompletableFuture<EdgeResultSet>> {

    private final ArgumentDeserializer argumentDeserializer;

    @Inject
    OutgoingEdgesDataFetcherImpl(ArgumentDeserializer argumentDeserializer) {
      this.argumentDeserializer = argumentDeserializer;
    }

    @Override
    public CompletableFuture<EdgeResultSet> get(DataFetchingEnvironment environment) {
      return CompletableFuture.completedFuture(
          environment
              .<Entity>getSource()
              .outgoingEdges(
                  getNeighbourEntityType(environment.getArguments()).orElse(null),
                  getNeighbourEntityScope(environment.getArguments()).orElse(null),
                  Collections.emptyList()));
    }

    private Optional<String> getNeighbourEntityScope(Map<String, Object> arguments) {
      return this.argumentDeserializer.deserializePrimitive(
          arguments, NeighborEntityScopeArgument.class);
    }

    private Optional<EntityType> getNeighbourEntityType(Map<String, Object> arguments) {
      return this.argumentDeserializer.deserializePrimitive(
          arguments, NeighborEntityTypeArgument.class);
    }
  }
}
