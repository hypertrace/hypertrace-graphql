package org.hypertrace.core.graphql.request.transformation;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.request.ContextualRequest;

public interface RequestTransformation {

  /**
   * Any request may be provided to this method, which should return true if this transformation
   * supports the request and false otherwise.
   */
  boolean supportsRequest(ContextualRequest request);

  /**
   * Applies a transformation to the request. This will only receive requests that have previously
   * returned true from {@link #supportsRequest(ContextualRequest)}, and must always return a
   * request, potentially the same request passed in if no transformation is necessary.
   */
  <T extends ContextualRequest> Single<T> transform(T request);
}
