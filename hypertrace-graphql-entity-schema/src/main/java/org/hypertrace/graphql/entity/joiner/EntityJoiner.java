package org.hypertrace.graphql.entity.joiner;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.entity.schema.Entity;

public interface EntityJoiner {
  /** A NOOP joiner that can be used when no entities need b joined */
  EntityJoiner NO_OP_JOINER =
      new EntityJoiner() {
        @Override
        public <T> Single<Table<T, String, Entity>> joinEntities(
            Collection<T> joinSources, EntityIdGetter<T> idGetter) {
          return Single.just(ImmutableTable.of());
        }
      };

  /**
   * Produces a table of entities to join to the source data Given a collection of source objects
   * and a getter to extract IDs out of each object by type,
   *
   * @param joinSources a collection of source data
   * @param idGetter A method that retrieves an ID of a specified entity type from an individual
   *     element of source data
   * @param <T> Type of source data
   * @return A Table representing one source per row. Each column corresponds to a requested entity
   *     type, with cells containing the join result for that type, if present. If no entity is
   *     present, the cell is empty.
   */
  <T> Single<Table<T, String, Entity>> joinEntities(
      Collection<T> joinSources, EntityIdGetter<T> idGetter);

  /**
   * Given a request context, source object and a desired entity type, returns the corresponding
   * entity ID, if any
   */
  @FunctionalInterface
  interface EntityIdGetter<T> {
    Maybe<String> getIdForType(GraphQlRequestContext requestContext, T source, String entityType);
  }
}
