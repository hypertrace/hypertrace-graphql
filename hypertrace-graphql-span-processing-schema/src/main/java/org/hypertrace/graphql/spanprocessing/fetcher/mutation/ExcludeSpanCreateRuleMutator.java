package org.hypertrace.graphql.spanprocessing.fetcher.mutation;

import static org.hypertrace.core.graphql.context.GraphQlRequestContext.contextFromEnvironment;

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
    super(ExcludeSpanCreateRuleMutatorImpl.class);
  }

  static final class ExcludeSpanCreateRuleMutatorImpl
      implements DataFetcher<CompletableFuture<ExcludeSpanRule>> {
    private final SpanProcessingRuleDao spanProcessingRuleDao;
    private final ExcludeSpanCreateRuleRequestBuilder requestBuilder;

    @Inject
    ExcludeSpanCreateRuleMutatorImpl(
        ExcludeSpanCreateRuleRequestBuilder requestBuilder,
        SpanProcessingRuleDao spanProcessingRuleDao) {
      this.requestBuilder = requestBuilder;
      this.spanProcessingRuleDao = spanProcessingRuleDao;
    }

    @Override
    public CompletableFuture<ExcludeSpanRule> get(DataFetchingEnvironment environment) {
      return this.requestBuilder
          .build(contextFromEnvironment(environment), environment.getArguments())
          .flatMap(this.spanProcessingRuleDao::createExcludeSpanRule)
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
