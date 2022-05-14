package com.scofu.command;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * A map that dynamically creates mappings as values are requested.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 */
public interface DynamicMap<K, V extends Predicate<K>> {

  /**
   * Adds a value.
   *
   * @param value the value
   */
  void add(V value);

  /**
   * Returns an optional value with the given key.
   *
   * @param key the key
   */
  Optional<V> get(K key);

  /**
   * Invalidates and returns an optional value with the given key.
   *
   * @param key the key
   */
  Optional<V> invalidate(K key);
}
