package com.scofu.command;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;

/**
 * Concurrent dynamic map implementation.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 */
public class ConcurrentDynamicMap<K, V extends Predicate<K>> implements DynamicMap<K, V> {

  private final Set<V> unmapped;
  private final Map<K, V> values;

  /** Constructs a new concurrent dynamic map. */
  public ConcurrentDynamicMap() {
    this.unmapped = new CopyOnWriteArraySet<>();
    this.values = new ConcurrentHashMap<>();
  }

  @Override
  public void add(V value) {
    checkNotNull(value, "value");
    unmapped.add(value);
  }

  @Override
  public Optional<V> get(K key) {
    checkNotNull(key, "key");
    var value = values.get(key);
    if (value == null) {
      for (var unmappedValue : unmapped) {
        if (unmappedValue.test(key)) {
          value = unmappedValue;
          values.put(key, value);
          break;
        }
      }
    }
    return Optional.ofNullable(value);
  }

  @Override
  public Optional<V> invalidate(K key) {
    checkNotNull(key, "key");
    return Optional.ofNullable(values.remove(key));
  }
}
