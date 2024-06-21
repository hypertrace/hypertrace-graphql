package org.hypertrace.core.graphql.common.utils;

import io.reactivex.rxjava3.core.Single;

public interface Converter<F, T> {

  Single<T> convert(F from);
}
