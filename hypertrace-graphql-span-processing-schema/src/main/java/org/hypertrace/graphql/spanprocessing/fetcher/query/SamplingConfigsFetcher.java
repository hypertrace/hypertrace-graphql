package org.hypertrace.graphql.spanprocessing.fetcher.query;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.core.graphql.common.request.ContextualRequestBuilder;
import org.hypertrace.graphql.spanprocessing.dao.SpanProcessingRuleDao;
import org.hypertrace.graphql.spanprocessing.schema.query.SamplingConfigsResultSet;

public class SamplingConfigsFetcher extends InjectableDataFetcher<SamplingConfigsResultSet> {

  public SamplingConfigsFetcher() {
    super(SamplingConfigsFetcherImpl.class);
  }

  static final class SamplingConfigsFetcherImpl
      implements DataFetcher<CompletableFuture<SamplingConfigsResultSet>> {
    private final SpanProcessingRuleDao spanProcessingRuleDao;
    private final ContextualRequestBuilder requestBuilder;

    @Inject
    SamplingConfigsFetcherImpl(
        ContextualRequestBuilder requestBuilder, SpanProcessingRuleDao spanProcessingRuleDao) {
      this.requestBuilder = requestBuilder;
      this.spanProcessingRuleDao = spanProcessingRuleDao;
    }

    @Override
    public CompletableFuture<SamplingConfigsResultSet> get(DataFetchingEnvironment environment) {
      return this.spanProcessingRuleDao
          .getSamplingConfigs(this.requestBuilder.build(environment.getContext()))
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
