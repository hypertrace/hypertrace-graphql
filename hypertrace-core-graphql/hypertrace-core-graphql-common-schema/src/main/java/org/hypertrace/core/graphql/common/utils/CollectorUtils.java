package org.hypertrace.core.graphql.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollectorUtils {

  public static <K, V> Collector<Entry<K, V>, ?, Map<K, V>> immutableMapEntryCollector() {
    return Collectors.toUnmodifiableMap(Entry::getKey, Entry::getValue);
  }

  public static <T, U> Collector<T, ?, List<U>> flatten(
      Function<T, Collection<U>> collectionMapper) {
    return flatten(collectionMapper, Collectors.toUnmodifiableList());
  }

  public static <T, U, C extends Collection<U>> Collector<T, ?, C> flatten(
      Function<T, Collection<U>> collectionMapper, Collector<U, ?, C> downstream) {
    return Collectors.flatMapping(collectionMapper.andThen(Collection::stream), downstream);
  }

  public static <T> Collector<T, ?, T> firstOrDefault(T defaultValue) {
    return Collectors.collectingAndThen(
        Collectors.toSet(), set -> set.stream().findFirst().orElse(defaultValue));
  }
}
