package org.hypertrace.core.graphql.common.utils;

import io.reactivex.rxjava3.core.Single;

/**
 * A Function, similar to {@link java.util.function.BiFunction} that accepts two arguments and
 * converts them to a result asynchronously.
 *
 * @param <F1>
 * @param <F2>
 * @param <T>
 */
public interface BiConverter<F1, F2, T> {

  Single<T> convert(F1 f1, F2 f2);
}
