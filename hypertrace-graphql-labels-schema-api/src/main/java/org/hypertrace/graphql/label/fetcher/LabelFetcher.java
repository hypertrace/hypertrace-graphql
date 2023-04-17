package org.hypertrace.graphql.label.fetcher;

import static org.hypertrace.core.graphql.context.GraphQlRequestContext.contextFromEnvironment;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.label.joiner.EntityAndRuleJoiner;
import org.hypertrace.graphql.label.joiner.EntityAndRuleJoinerBuilder;
import org.hypertrace.graphql.label.schema.LabelResultSet;

public class LabelFetcher extends InjectableDataFetcher<LabelResultSet> {

  public LabelFetcher() {
    super(LabelFetcherImpl.class);
  }

  static final class LabelFetcherImpl implements DataFetcher<CompletableFuture<LabelResultSet>> {
    private final EntityAndRuleJoinerBuilder requestBuilder;

    @Inject
    LabelFetcherImpl(EntityAndRuleJoinerBuilder requestBuilder) {
      this.requestBuilder = requestBuilder;
    }

    @Override
    public CompletableFuture<LabelResultSet> get(DataFetchingEnvironment environment) {
      return this.requestBuilder
          .build(contextFromEnvironment(environment), environment.getSelectionSet())
          .flatMap(EntityAndRuleJoiner::joinLabelsWithEntitiesAndRules)
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
