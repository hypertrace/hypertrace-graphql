package org.hypertrace.graphql.spanprocessing.fetcher.mutation;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.spanprocessing.dao.SpanProcessingRuleDao;
import org.hypertrace.graphql.spanprocessing.request.mutation.SamplingConfigDeleteRequestBuilder;
import org.hypertrace.graphql.spanprocessing.schema.mutation.DeleteSpanProcessingRuleResponse;

public class SamplingConfigDeleteMutator
    extends InjectableDataFetcher<DeleteSpanProcessingRuleResponse> {

  public SamplingConfigDeleteMutator() {
    super(SamplingConfigDeleteMutatorImpl.class);
  }

  static final class SamplingConfigDeleteMutatorImpl
      implements DataFetcher<CompletableFuture<DeleteSpanProcessingRuleResponse>> {
    private final SpanProcessingRuleDao spanProcessingRuleDao;
    private final SamplingConfigDeleteRequestBuilder requestBuilder;

    @Inject
    SamplingConfigDeleteMutatorImpl(
        SamplingConfigDeleteRequestBuilder requestBuilder,
        SpanProcessingRuleDao spanProcessingRuleDao) {
      this.requestBuilder = requestBuilder;
      this.spanProcessingRuleDao = spanProcessingRuleDao;
    }

    @Override
    public CompletableFuture<DeleteSpanProcessingRuleResponse> get(
        DataFetchingEnvironment environment) {
      return this.requestBuilder
          .build(environment.getContext(), environment.getArguments())
          .flatMap(this.spanProcessingRuleDao::deleteSamplingConfig)
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
