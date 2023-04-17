package org.hypertrace.graphql.spanprocessing.fetcher.mutation;

import static org.hypertrace.core.graphql.context.GraphQlRequestContext.contextFromEnvironment;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.spanprocessing.dao.SpanProcessingRuleDao;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanUpdateRuleRequestBuilder;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRule;

public class ExcludeSpanUpdateRuleMutator extends InjectableDataFetcher<ExcludeSpanRule> {

  public ExcludeSpanUpdateRuleMutator() {
    super(ExcludeSpanUpdateRuleMutatorImpl.class);
  }

  static final class ExcludeSpanUpdateRuleMutatorImpl
      implements DataFetcher<CompletableFuture<ExcludeSpanRule>> {
    private final SpanProcessingRuleDao spanProcessingRuleDao;
    private final ExcludeSpanUpdateRuleRequestBuilder requestBuilder;

    @Inject
    ExcludeSpanUpdateRuleMutatorImpl(
        ExcludeSpanUpdateRuleRequestBuilder requestBuilder,
        SpanProcessingRuleDao spanProcessingRuleDao) {
      this.requestBuilder = requestBuilder;
      this.spanProcessingRuleDao = spanProcessingRuleDao;
    }

    @Override
    public CompletableFuture<ExcludeSpanRule> get(DataFetchingEnvironment environment) {
      return this.requestBuilder
          .build(contextFromEnvironment(environment), environment.getArguments())
          .flatMap(this.spanProcessingRuleDao::updateExcludeSpanRule)
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
