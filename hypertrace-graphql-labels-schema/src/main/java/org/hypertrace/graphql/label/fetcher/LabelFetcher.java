package org.hypertrace.graphql.label.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.core.graphql.common.request.ContextualRequestBuilder;
import org.hypertrace.graphql.label.dao.LabelDao;
import org.hypertrace.graphql.label.schema.query.LabelResultSet;

public class LabelFetcher extends InjectableDataFetcher<LabelResultSet> {

  public LabelFetcher() {
    super(LabelFetcherImpl.class);
  }

  static final class LabelFetcherImpl implements DataFetcher<CompletableFuture<LabelResultSet>> {
    private final ContextualRequestBuilder requestBuilder;
    private final LabelDao labelDao;

    @Inject
    LabelFetcherImpl(ContextualRequestBuilder requestBuilder, LabelDao labelDao) {
      this.requestBuilder = requestBuilder;
      this.labelDao = labelDao;
    }

    @Override
    public CompletableFuture<LabelResultSet> get(DataFetchingEnvironment environment) {
      return this.labelDao
          .getLabels(this.requestBuilder.build(environment.getContext()))
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
