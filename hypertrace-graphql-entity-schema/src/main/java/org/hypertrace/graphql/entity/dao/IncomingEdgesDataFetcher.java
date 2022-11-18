package org.hypertrace.graphql.entity.dao;

import java.util.List;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.entity.schema.EdgeResultSet;
import org.hypertrace.graphql.entity.schema.Entity;
import org.hypertrace.graphql.entity.schema.EntityType;

public class IncomingEdgesDataFetcher extends InjectableDataFetcher<EdgeResultSet> {
  public IncomingEdgesDataFetcher() {
    super(IncomingEdgesDataFetcherImpl.class);
  }

  private static class IncomingEdgesDataFetcherImpl extends EdgesDataFetcherImpl {

    @Inject
    IncomingEdgesDataFetcherImpl(ArgumentDeserializer argumentDeserializer) {
      super(argumentDeserializer);
    }

    @Override
    protected EdgeResultSet getEdges(
        Entity entity,
        EntityType neighborType,
        String neighborScope,
        List<FilterArgument> filterBy) {
      return entity.incomingEdges(neighborType, neighborScope, filterBy);
    }
  }
}
