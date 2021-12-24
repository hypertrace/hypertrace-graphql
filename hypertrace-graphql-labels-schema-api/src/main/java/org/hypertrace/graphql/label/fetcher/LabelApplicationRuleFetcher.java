package org.hypertrace.graphql.label.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.core.graphql.common.request.ContextualRequestBuilder;
import org.hypertrace.graphql.label.dao.LabelApplicationRuleDao;
import org.hypertrace.graphql.label.schema.LabelApplicationRuleResultSet;

public class LabelApplicationRuleFetcher
    extends InjectableDataFetcher<LabelApplicationRuleResultSet> {
  public LabelApplicationRuleFetcher() {
    super(LabelApplicationRuleFetcherImpl.class);
  }

  static final class LabelApplicationRuleFetcherImpl
      implements DataFetcher<CompletableFuture<LabelApplicationRuleResultSet>> {
    private final ContextualRequestBuilder requestBuilder;
    private final LabelApplicationRuleDao labelApplicationRuleDao;

    @Inject
    LabelApplicationRuleFetcherImpl(
        ContextualRequestBuilder requestBuilder, LabelApplicationRuleDao labelApplicationRuleDao) {
      this.requestBuilder = requestBuilder;
      this.labelApplicationRuleDao = labelApplicationRuleDao;
    }

    @Override
    public CompletableFuture<LabelApplicationRuleResultSet> get(
        DataFetchingEnvironment environment) {
      return this.labelApplicationRuleDao
          .getLabelApplicationRules(this.requestBuilder.build(environment.getContext()))
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
