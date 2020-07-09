package org.hypertrace.core.graphql.metadata.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.core.graphql.metadata.response.MetadataResponseBuilder;
import org.hypertrace.core.graphql.metadata.schema.AttributeMetadata;

public class MetadataFetcher extends InjectableDataFetcher<List<AttributeMetadata>> {

  public MetadataFetcher() {
    super(MetadataFetcherImpl.class);
  }

  static final class MetadataFetcherImpl
      implements DataFetcher<CompletableFuture<List<AttributeMetadata>>> {
    private final MetadataResponseBuilder responseBuilder;
    private final AttributeStore attributeStore;

    @Inject
    MetadataFetcherImpl(MetadataResponseBuilder responseBuilder, AttributeStore attributeStore) {
      this.responseBuilder = responseBuilder;
      this.attributeStore = attributeStore;
    }

    @Override
    public CompletableFuture<List<AttributeMetadata>> get(DataFetchingEnvironment environment) {
      return this.attributeStore
          .getAll(environment.getContext())
          .flatMap(this.responseBuilder::build)
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
