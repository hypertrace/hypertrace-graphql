package org.hypertrace.graphql.spanprocessing.fetcher.mutation;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.spanprocessing.dao.SpanProcessingRuleDao;
import org.hypertrace.graphql.spanprocessing.request.mutation.SamplingConfigCreateRequestBuilder;
import org.hypertrace.graphql.spanprocessing.schema.samplingconfigs.SamplingConfig;

public class SamplingConfigCreateMutator extends InjectableDataFetcher<SamplingConfig> {

  public SamplingConfigCreateMutator() {
    super(SamplingConfigCreateMutatorImpl.class);
  }

  static final class SamplingConfigCreateMutatorImpl
      implements DataFetcher<CompletableFuture<SamplingConfig>> {
    private final SpanProcessingRuleDao spanProcessingRuleDao;
    private final SamplingConfigCreateRequestBuilder requestBuilder;

    @Inject
    SamplingConfigCreateMutatorImpl(
        SamplingConfigCreateRequestBuilder requestBuilder,
        SpanProcessingRuleDao spanProcessingRuleDao) {
      this.requestBuilder = requestBuilder;
      this.spanProcessingRuleDao = spanProcessingRuleDao;
    }

    @Override
    public CompletableFuture<SamplingConfig> get(DataFetchingEnvironment environment) {
      return this.requestBuilder
          .build(environment.getContext(), environment.getArguments())
          .flatMap(this.spanProcessingRuleDao::createSamplingConfig)
          .toCompletionStage()
          .toCompletableFuture();
    }
  }
}
