package org.hypertrace.core.graphql.request.transformation;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Set;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.request.ContextualRequest;

class RequestTransformerImpl implements RequestTransformer {
  private final Set<RequestTransformation> requestTransformations;

  @Inject
  RequestTransformerImpl(Set<RequestTransformation> requestTransformations) {
    this.requestTransformations = requestTransformations;
  }

  @Override
  public <T extends ContextualRequest> Single<T> transform(T request) {
    return Observable.fromIterable(requestTransformations)
        .filter(requestTransformation -> requestTransformation.supportsRequest(request))
        .reduce(
            Single.just(request), // Work in singles since there's no flat reduce, then unwrap later
            (currentRequestSingle, requestTransformation) ->
                currentRequestSingle.flatMap(requestTransformation::transform))
        .flatMap(wrapped -> wrapped);
  }
}
