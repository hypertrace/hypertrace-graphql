package org.hypertrace.graphql.label.mutator;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.label.dao.LabelDao;
import org.hypertrace.graphql.label.request.LabelRequestBuilder;
import org.hypertrace.graphql.label.schema.query.Label;

public class LabelCreateMutator extends InjectableDataFetcher<Label> {

  public LabelCreateMutator() {
    super(LabelCreationMutatorImpl.class);
  }

  static final class LabelCreationMutatorImpl implements DataFetcher<CompletableFuture<Label>> {
    private final LabelDao configDao;
    private final LabelRequestBuilder requestBuilder;

    @Inject
    LabelCreationMutatorImpl(LabelDao configDao, LabelRequestBuilder requestBuilder) {
      this.configDao = configDao;
      this.requestBuilder = requestBuilder;
    }

    @Override
    public CompletableFuture<Label> get(DataFetchingEnvironment environment) {
      return this.configDao
          .createLabel(
              this.requestBuilder.buildCreateRequest(
                  environment.getContext(), environment.getArguments()))
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
