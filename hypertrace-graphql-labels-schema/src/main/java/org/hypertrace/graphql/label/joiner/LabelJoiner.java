package org.hypertrace.graphql.label.joiner;

import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hypertrace.graphql.label.schema.LabelResultSet;

public interface LabelJoiner {

  /** A NOOP joiner */
  LabelJoiner NO_OP_JOINER =
      new LabelJoiner() {
        @Override
        public <T> Single<Map<T, LabelResultSet>> joinLabels(
            Collection<T> joinSources, LabelIdGetter<T> labelIdGetter) {
          return Single.just(Collections.emptyMap());
        }
      };

  /**
   * Produces a map of label result set to source ids
   *
   * @param joinSources a collection of source data
   * @param labelIdGetter A method that retrieves labels from the source
   * @param <T> Type of source data
   * @return A map of each source to its matching label result set
   */
  <T> Single<Map<T, LabelResultSet>> joinLabels(
      Collection<T> joinSources, LabelIdGetter<T> labelIdGetter);

  @FunctionalInterface
  interface LabelIdGetter<T> {
    List<String> getLabelIds(T source);
  }
}
