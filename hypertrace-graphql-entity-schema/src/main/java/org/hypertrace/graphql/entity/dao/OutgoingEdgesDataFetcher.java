package org.hypertrace.graphql.entity.dao;

import java.util.List;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.graphql.entity.schema.EdgeResultSet;
import org.hypertrace.graphql.entity.schema.Entity;
import org.hypertrace.graphql.entity.schema.EntityType;

public class OutgoingEdgesDataFetcher extends InjectableDataFetcher<EdgeResultSet> {

  public OutgoingEdgesDataFetcher() {
    super(OutgoingEdgesDataFetcherImpl.class);
  }

  private static class OutgoingEdgesDataFetcherImpl extends EdgesDataFetcherImpl {

    @Inject
    OutgoingEdgesDataFetcherImpl(ArgumentDeserializer argumentDeserializer) {
      super(argumentDeserializer);
    }

    @Override
    protected EdgeResultSet getEdges(
        Entity entity,
        EntityType neighborType,
        String neighborScope,
        List<FilterArgument> filterBy) {
      return entity.outgoingEdges(neighborType, neighborScope, filterBy);
    }
  }
}
