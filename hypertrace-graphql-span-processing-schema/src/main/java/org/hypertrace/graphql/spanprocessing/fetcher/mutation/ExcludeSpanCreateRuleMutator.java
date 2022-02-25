package org.hypertrace.graphql.spanprocessing.fetcher.mutation;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.spanprocessing.dao.SpanProcessingRuleDao;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanCreateRuleRequestBuilder;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRule;

public class ExcludeSpanCreateRuleMutator extends InjectableDataFetcher<ExcludeSpanRule> {

  public ExcludeSpanCreateRuleMutator() {
    super(SpanProcessingCreateRuleMutatorImpl.class);
  }

  static final class SpanProcessingCreateRuleMutatorImpl
      implements DataFetcher<CompletableFuture<ExcludeSpanRule>> {
    private final SpanProcessingRuleDao spanProcessingRuleDao;
    private final ExcludeSpanCreateRuleRequestBuilder requestBuilder;

    @Inject
    SpanProcessingCreateRuleMutatorImpl(
        ExcludeSpanCreateRuleRequestBuilder requestBuilder,
        SpanProcessingRuleDao spanProcessingRuleDao) {
      this.requestBuilder = requestBuilder;
      this.spanProcessingRuleDao = spanProcessingRuleDao;
    }

    @Override
    public CompletableFuture<ExcludeSpanRule> get(DataFetchingEnvironment environment) {
      return this.requestBuilder
          .build(environment.getContext(), environment.getArguments())
          .flatMap(this.spanProcessingRuleDao::createRule)
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
