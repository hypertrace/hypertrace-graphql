package org.hypertrace.graphql.label.mutator;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.label.dao.LabelDao;
import org.hypertrace.graphql.label.request.LabelsConfigRequestBuilder;

public class LabelDeleteMutator extends InjectableDataFetcher<Boolean> {
  public LabelDeleteMutator() {
    super(LabelDeletionMutatorImpl.class);
  }

  static final class LabelDeletionMutatorImpl implements DataFetcher<CompletableFuture<Boolean>> {
    private final LabelDao configDao;
    private final LabelsConfigRequestBuilder requestBuilder;

    @Inject
    LabelDeletionMutatorImpl(LabelDao configDao, LabelsConfigRequestBuilder requestBuilder) {
      this.configDao = configDao;
      this.requestBuilder = requestBuilder;
    }

    @Override
    public CompletableFuture<Boolean> get(DataFetchingEnvironment environment) {
      return this.configDao
          .deleteLabel(
              this.requestBuilder.buildDeleteRequest(
                  environment.getContext(), environment.getArguments()))
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
