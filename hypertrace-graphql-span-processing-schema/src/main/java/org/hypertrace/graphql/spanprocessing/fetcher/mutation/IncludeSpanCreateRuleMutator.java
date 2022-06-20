package org.hypertrace.graphql.spanprocessing.fetcher.mutation;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.spanprocessing.dao.SpanProcessingRuleDao;
import org.hypertrace.graphql.spanprocessing.request.mutation.IncludeSpanCreateRuleRequestBuilder;
import org.hypertrace.graphql.spanprocessing.schema.rule.IncludeSpanRule;

public class IncludeSpanCreateRuleMutator extends InjectableDataFetcher<IncludeSpanRule> {

  public IncludeSpanCreateRuleMutator() {
    super(IncludeSpanCreateRuleMutatorImpl.class);
  }

  static final class IncludeSpanCreateRuleMutatorImpl
      implements DataFetcher<CompletableFuture<IncludeSpanRule>> {
    private final SpanProcessingRuleDao spanProcessingRuleDao;
    private final IncludeSpanCreateRuleRequestBuilder requestBuilder;

    @Inject
    IncludeSpanCreateRuleMutatorImpl(
        IncludeSpanCreateRuleRequestBuilder requestBuilder,
        SpanProcessingRuleDao spanProcessingRuleDao) {
      this.requestBuilder = requestBuilder;
      this.spanProcessingRuleDao = spanProcessingRuleDao;
    }

    @Override
    public CompletableFuture<IncludeSpanRule> get(DataFetchingEnvironment environment) {
      return this.requestBuilder
          .build(environment.getContext(), environment.getArguments())
          .flatMap(this.spanProcessingRuleDao::createIncludeSpanRule)
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
