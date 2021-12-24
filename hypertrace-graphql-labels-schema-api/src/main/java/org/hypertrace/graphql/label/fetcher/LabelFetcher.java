package org.hypertrace.graphql.label.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.label.dao.LabelDao;
import org.hypertrace.graphql.label.joiner.LabelJoinerBuilder;
import org.hypertrace.graphql.label.schema.LabelResultSet;

public class LabelFetcher extends InjectableDataFetcher<LabelResultSet> {

  public LabelFetcher() {
    super(LabelFetcherImpl.class);
  }

  static final class LabelFetcherImpl implements DataFetcher<CompletableFuture<LabelResultSet>> {
    private final LabelJoinerBuilder requestBuilder;
    private final LabelDao labelDao;

    @Inject
    LabelFetcherImpl(LabelJoinerBuilder requestBuilder, LabelDao labelDao) {
      this.requestBuilder = requestBuilder;
      this.labelDao = labelDao;
    }

    @Override
    public CompletableFuture<LabelResultSet> get(DataFetchingEnvironment environment) {
      return this.requestBuilder
          .build(environment.getContext(), environment.getSelectionSet())
          .flatMap(request -> request.joinLabelsWithEntities())
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
