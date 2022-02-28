package org.hypertrace.graphql.spanprocessing.fetcher.query;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.core.graphql.common.request.ContextualRequestBuilder;
import org.hypertrace.graphql.spanprocessing.dao.SpanProcessingRuleDao;
import org.hypertrace.graphql.spanprocessing.schema.query.ApiNamingRuleResultSet;

public class ApiNamingRulesFetcher extends InjectableDataFetcher<ApiNamingRuleResultSet> {

  public ApiNamingRulesFetcher() {
    super(ApiNamingRulesFetcherImpl.class);
  }

  static final class ApiNamingRulesFetcherImpl
      implements DataFetcher<CompletableFuture<ApiNamingRuleResultSet>> {
    private final SpanProcessingRuleDao spanProcessingRuleDao;
    private final ContextualRequestBuilder requestBuilder;

    @Inject
    ApiNamingRulesFetcherImpl(
        ContextualRequestBuilder requestBuilder, SpanProcessingRuleDao spanProcessingRuleDao) {
      this.requestBuilder = requestBuilder;
      this.spanProcessingRuleDao = spanProcessingRuleDao;
    }

    @Override
    public CompletableFuture<ApiNamingRuleResultSet> get(DataFetchingEnvironment environment) {
      return this.spanProcessingRuleDao
          .getApiNamingRules(this.requestBuilder.build(environment.getContext()))
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
