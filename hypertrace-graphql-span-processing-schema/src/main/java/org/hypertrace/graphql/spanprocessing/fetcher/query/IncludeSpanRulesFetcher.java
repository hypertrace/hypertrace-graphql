package org.hypertrace.graphql.spanprocessing.fetcher.query;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.core.graphql.common.request.ContextualRequestBuilder;
import org.hypertrace.graphql.spanprocessing.dao.SpanProcessingRuleDao;
import org.hypertrace.graphql.spanprocessing.schema.query.IncludeSpanRuleResultSet;

public class IncludeSpanRulesFetcher extends InjectableDataFetcher<IncludeSpanRuleResultSet> {

  public IncludeSpanRulesFetcher() {
    super(IncludeSpanRulesFetcherImpl.class);
  }

  static final class IncludeSpanRulesFetcherImpl
      implements DataFetcher<CompletableFuture<IncludeSpanRuleResultSet>> {
    private final SpanProcessingRuleDao spanProcessingRuleDao;
    private final ContextualRequestBuilder requestBuilder;

    @Inject
    IncludeSpanRulesFetcherImpl(
        ContextualRequestBuilder requestBuilder, SpanProcessingRuleDao spanProcessingRuleDao) {
      this.requestBuilder = requestBuilder;
      this.spanProcessingRuleDao = spanProcessingRuleDao;
    }

    @Override
    public CompletableFuture<IncludeSpanRuleResultSet> get(DataFetchingEnvironment environment) {
      return this.spanProcessingRuleDao
          .getIncludeSpanRules(this.requestBuilder.build(environment.getContext()))
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
