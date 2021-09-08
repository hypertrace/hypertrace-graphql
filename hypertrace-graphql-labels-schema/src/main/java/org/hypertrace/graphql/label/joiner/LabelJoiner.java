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
        public <T> Single<Map<String, LabelResultSet>> joinLabels(
            Collection<T> joinSources, IdGetter<T> idGetter, LabelsGetter<T> labelsGetter) {
          return Single.just(Collections.emptyMap());
        }
      };

  /**
   * Produces a map of label result set to source ids
   *
   * @param joinSources a collection of source data
   * @param idGetter A method that retrieves an ID from the source
   * @param labelsGetter A method that retrieves labels from the source
   * @param <T> Type of source data
   * @return A map of id of each source to its matching label result set
   */
  <T> Single<Map<String, LabelResultSet>> joinLabels(
      Collection<T> joinSources, IdGetter<T> idGetter, LabelsGetter<T> labelsGetter);

  @FunctionalInterface
  interface IdGetter<T> {
    String getId(T source);
  }

  @FunctionalInterface
  interface LabelsGetter<T> {
    List<String> getLabels(T source);
  }
}
