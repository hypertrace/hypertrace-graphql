package org.hypertrace.core.graphql.request.transformation;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.request.ContextualRequest;

/**
 * A request transformer that can receive any request and will apply all applicable transformations
 * that have been registered (currently, the order is undefined), returning the result.
 */
public interface RequestTransformer {
  <T extends ContextualRequest> Single<T> transform(T request);
}
