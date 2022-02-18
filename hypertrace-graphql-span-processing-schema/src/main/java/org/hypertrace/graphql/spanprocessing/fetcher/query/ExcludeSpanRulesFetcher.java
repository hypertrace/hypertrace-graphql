package org.hypertrace.graphql.spanprocessing.fetcher.query;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.core.graphql.common.request.ContextualRequestBuilder;
import org.hypertrace.graphql.spanprocessing.dao.SpanProcessingRuleDao;
import org.hypertrace.graphql.spanprocessing.schema.query.ExcludeSpanRuleResultSet;

public class ExcludeSpanRulesFetcher extends InjectableDataFetcher<ExcludeSpanRuleResultSet> {

  public ExcludeSpanRulesFetcher() {
    super(SpanProcessingRulesFetcherImpl.class);
  }

  static final class SpanProcessingRulesFetcherImpl
      implements DataFetcher<CompletableFuture<ExcludeSpanRuleResultSet>> {
    private final SpanProcessingRuleDao spanProcessingRuleDao;
    private final ContextualRequestBuilder requestBuilder;

    @Inject
    SpanProcessingRulesFetcherImpl(
        ContextualRequestBuilder requestBuilder, SpanProcessingRuleDao spanProcessingRuleDao) {
      this.requestBuilder = requestBuilder;
      this.spanProcessingRuleDao = spanProcessingRuleDao;
    }

    @Override
    public CompletableFuture<ExcludeSpanRuleResultSet> get(DataFetchingEnvironment environment) {

      return this.spanProcessingRuleDao
          .getRules(this.requestBuilder.build(environment.getContext()))
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
