package org.hypertrace.graphql.spanprocessing.fetcher.mutation;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.spanprocessing.dao.SpanProcessingRuleDao;
import org.hypertrace.graphql.spanprocessing.request.mutation.ApiNamingCreateRuleRequestBuilder;
import org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRule;

public class ApiNamingCreateRuleMutator extends InjectableDataFetcher<ApiNamingRule> {

  public ApiNamingCreateRuleMutator() {
    super(ApiNamingCreateRuleMutatorImpl.class);
  }

  static final class ApiNamingCreateRuleMutatorImpl
      implements DataFetcher<CompletableFuture<ApiNamingRule>> {
    private final SpanProcessingRuleDao spanProcessingRuleDao;
    private final ApiNamingCreateRuleRequestBuilder requestBuilder;

    @Inject
    ApiNamingCreateRuleMutatorImpl(
        ApiNamingCreateRuleRequestBuilder requestBuilder,
        SpanProcessingRuleDao spanProcessingRuleDao) {
      this.requestBuilder = requestBuilder;
      this.spanProcessingRuleDao = spanProcessingRuleDao;
    }

    @Override
    public CompletableFuture<ApiNamingRule> get(DataFetchingEnvironment environment) {
      return this.requestBuilder
          .build(environment.getContext(), environment.getArguments())
          .flatMap(this.spanProcessingRuleDao::createApiNamingRule)
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
