package org.hypertrace.graphql.spanprocessing.fetcher.query;

import static org.hypertrace.core.graphql.context.GraphQlRequestContext.contextFromEnvironment;

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
    super(ExcludeSpanRulesFetcherImpl.class);
  }

  static final class ExcludeSpanRulesFetcherImpl
      implements DataFetcher<CompletableFuture<ExcludeSpanRuleResultSet>> {
    private final SpanProcessingRuleDao spanProcessingRuleDao;
    private final ContextualRequestBuilder requestBuilder;

    @Inject
    ExcludeSpanRulesFetcherImpl(
        ContextualRequestBuilder requestBuilder, SpanProcessingRuleDao spanProcessingRuleDao) {
      this.requestBuilder = requestBuilder;
      this.spanProcessingRuleDao = spanProcessingRuleDao;
    }

    @Override
    public CompletableFuture<ExcludeSpanRuleResultSet> get(DataFetchingEnvironment environment) {
      return this.spanProcessingRuleDao
          .getExcludeSpanRules(this.requestBuilder.build(contextFromEnvironment(environment)))
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
