package org.hypertrace.graphql.label.application.rules.mutator;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.label.application.rules.dao.LabelApplicationRuleDao;
import org.hypertrace.graphql.label.application.rules.request.LabelApplicationRuleRequestBuilder;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRule;

public class LabelApplicationRuleUpdateMutator extends InjectableDataFetcher<LabelApplicationRule> {

  public LabelApplicationRuleUpdateMutator() {
    super(LabelApplicationRuleUpdateMutatorImpl.class);
  }

  static final class LabelApplicationRuleUpdateMutatorImpl
      implements DataFetcher<CompletableFuture<LabelApplicationRule>> {
    private final LabelApplicationRuleRequestBuilder requestBuilder;
    private final LabelApplicationRuleDao labelApplicationRuleDao;

    @Inject
    LabelApplicationRuleUpdateMutatorImpl(
        LabelApplicationRuleRequestBuilder requestBuilder,
        LabelApplicationRuleDao labelApplicationRuleDao) {
      this.requestBuilder = requestBuilder;
      this.labelApplicationRuleDao = labelApplicationRuleDao;
    }

    @Override
    public CompletableFuture<LabelApplicationRule> get(DataFetchingEnvironment environment) {
      return this.labelApplicationRuleDao
          .updateLabelApplicationRule(
              this.requestBuilder.buildUpdateLabelApplicationRuleRequest(
                  environment.getContext(), environment.getArguments()))
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
