package org.hypertrace.graphql.label.mutator;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.label.dao.LabelApplicationRuleDao;
import org.hypertrace.graphql.label.request.LabelApplicationRuleRequestBuilder;
import org.hypertrace.graphql.label.schema.rule.LabelApplicationRule;

public class LabelApplicationRuleCreateMutator extends InjectableDataFetcher<LabelApplicationRule> {

  public LabelApplicationRuleCreateMutator() {
    super(LabelApplicationRuleCreateMutatorImpl.class);
  }

  static final class LabelApplicationRuleCreateMutatorImpl
      implements DataFetcher<CompletableFuture<LabelApplicationRule>> {
    private final LabelApplicationRuleRequestBuilder requestBuilder;
    private final LabelApplicationRuleDao labelApplicationRuleDao;

    @Inject
    LabelApplicationRuleCreateMutatorImpl(
        LabelApplicationRuleRequestBuilder requestBuilder,
        LabelApplicationRuleDao labelApplicationRuleDao) {
      this.requestBuilder = requestBuilder;
      this.labelApplicationRuleDao = labelApplicationRuleDao;
    }

    @Override
    public CompletableFuture<LabelApplicationRule> get(DataFetchingEnvironment environment) {
      return this.labelApplicationRuleDao
          .createLabelApplicationRule(
              this.requestBuilder.buildCreateLabelApplicationRuleRequest(
                  environment.getContext(), environment.getArguments()))
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
