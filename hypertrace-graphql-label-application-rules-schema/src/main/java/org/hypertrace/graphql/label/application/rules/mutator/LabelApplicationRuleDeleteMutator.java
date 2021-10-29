package org.hypertrace.graphql.label.application.rules.mutator;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.label.application.rules.dao.LabelApplicationRuleDao;
import org.hypertrace.graphql.label.application.rules.request.LabelApplicationRuleRequestBuilder;

public class LabelApplicationRuleDeleteMutator extends InjectableDataFetcher<Boolean> {
  public LabelApplicationRuleDeleteMutator() {
    super(LabelApplicationRuleDeleteMutatorImpl.class);
  }

  static final class LabelApplicationRuleDeleteMutatorImpl
      implements DataFetcher<CompletableFuture<Boolean>> {
    private final LabelApplicationRuleRequestBuilder requestBuilder;
    private final LabelApplicationRuleDao labelApplicationRuleDao;

    @Inject
    LabelApplicationRuleDeleteMutatorImpl(
        LabelApplicationRuleRequestBuilder requestBuilder,
        LabelApplicationRuleDao labelApplicationRuleDao) {
      this.requestBuilder = requestBuilder;
      this.labelApplicationRuleDao = labelApplicationRuleDao;
    }

    @Override
    public CompletableFuture<Boolean> get(DataFetchingEnvironment environment) {
      return this.labelApplicationRuleDao
          .deleteLabelApplicationRule(
              this.requestBuilder.buildDeleteLabelApplicationRuleRequest(
                  environment.getContext(), environment.getArguments()))
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
