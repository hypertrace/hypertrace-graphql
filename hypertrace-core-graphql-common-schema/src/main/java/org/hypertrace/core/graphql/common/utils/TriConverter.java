package org.hypertrace.core.graphql.common.utils;

import io.reactivex.rxjava3.core.Single;

public interface TriConverter<F1, F2, F3, T> {
    Single<T> convert(F1 f1, F2 f2, F3 f3);
}
