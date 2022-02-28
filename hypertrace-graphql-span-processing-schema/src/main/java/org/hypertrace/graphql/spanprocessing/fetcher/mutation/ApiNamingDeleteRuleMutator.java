package org.hypertrace.graphql.spanprocessing.fetcher.mutation;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.spanprocessing.dao.SpanProcessingRuleDao;
import org.hypertrace.graphql.spanprocessing.request.mutation.ApiNamingDeleteRuleRequestBuilder;
import org.hypertrace.graphql.spanprocessing.schema.mutation.DeleteSpanProcessingRuleResponse;

public class ApiNamingDeleteRuleMutator
    extends InjectableDataFetcher<DeleteSpanProcessingRuleResponse> {

  public ApiNamingDeleteRuleMutator() {
    super(ApiNamingDeleteRuleMutatorImpl.class);
  }

  static final class ApiNamingDeleteRuleMutatorImpl
      implements DataFetcher<CompletableFuture<DeleteSpanProcessingRuleResponse>> {
    private final SpanProcessingRuleDao spanProcessingRuleDao;
    private final ApiNamingDeleteRuleRequestBuilder requestBuilder;

    @Inject
    ApiNamingDeleteRuleMutatorImpl(
        ApiNamingDeleteRuleRequestBuilder requestBuilder,
        SpanProcessingRuleDao spanProcessingRuleDao) {
      this.requestBuilder = requestBuilder;
      this.spanProcessingRuleDao = spanProcessingRuleDao;
    }

    @Override
    public CompletableFuture<DeleteSpanProcessingRuleResponse> get(
        DataFetchingEnvironment environment) {
      return this.requestBuilder
          .build(environment.getContext(), environment.getArguments())
          .flatMap(this.spanProcessingRuleDao::deleteApiNamingRule)
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
