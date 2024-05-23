package org.hypertrace.core.graphql.span.joiner;

import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.hypertrace.core.graphql.span.schema.Span;

public interface SpanJoiner {

  /** A NOOP joiner */
  SpanJoiner NO_OP_JOINER =
      new SpanJoiner() {
        @Override
        public <T> Single<Map<T, Span>> joinSpans(
            Collection<T> joinSources, SpanIdGetter<T> spanIdGetter) {
          return Single.just(Collections.emptyMap());
        }
      };

  <T> Single<Map<T, Span>> joinSpans(Collection<T> joinSources, SpanIdGetter<T> spanIdGetter);

  @FunctionalInterface
  interface SpanIdGetter<T> {
    Single<String> getSpanId(T source);
  }
}
