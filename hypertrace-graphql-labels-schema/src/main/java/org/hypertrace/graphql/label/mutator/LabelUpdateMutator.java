package org.hypertrace.graphql.label.mutator;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.hypertrace.core.graphql.common.fetcher.InjectableDataFetcher;
import org.hypertrace.graphql.label.dao.LabelDao;
import org.hypertrace.graphql.label.request.LabelsConfigRequestBuilder;
import org.hypertrace.graphql.label.schema.Label;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class LabelUpdateMutator extends InjectableDataFetcher<Label> {

    public LabelUpdateMutator() {
        super(LabelUpdateMutatorImpl.class);
    }

    static final class LabelUpdateMutatorImpl
            implements DataFetcher<CompletableFuture<Label>> {
        private final LabelDao configDao;
        private final LabelsConfigRequestBuilder requestBuilder;

        @Inject
        LabelUpdateMutatorImpl(
                LabelDao configDao, LabelsConfigRequestBuilder requestBuilder) {
            this.configDao = configDao;
            this.requestBuilder = requestBuilder;
        }

        @Override
        public CompletableFuture<Label> get(DataFetchingEnvironment environment) {
            return this.configDao
                    .updateLabel(
                            this.requestBuilder.buildUpdateRequest(
                                    environment.getContext(), environment.getArguments()))
                    .toCompletionStage()
                    .toCompletableFuture();
        }
    }
}
