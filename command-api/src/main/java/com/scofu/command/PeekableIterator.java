package com.scofu.command;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Optional;

/**
 * A peekable iterator.
 *
 * @param <T> the type of the elements
 */
public class PeekableIterator<T> implements Iterator<T> {

  private final Iterator<T> iterator;
  private T peek;

  private PeekableIterator(Iterator<T> iterator) {
    this.iterator = iterator;
  }

  /**
   * Wraps and returns the given iterator in a new peekable iterator.
   *
   * @param iterator the iterator
   * @param <T> the type of the elements
   */
  public static <T> PeekableIterator<T> wrap(Iterator<T> iterator) {
    checkNotNull(iterator, "iterator");
    return new PeekableIterator<>(iterator);
  }

  @Override
  public boolean hasNext() {
    return peek != null || iterator.hasNext();
  }

  @Override
  public T next() {
    if (peek != null) {
      T next = peek;
      peek = null;
      return next;
    }
    return iterator.next();
  }

  /** Peeks at the next value. */
  public Optional<T> peek() {
    return peek != null
        ? Optional.of(peek)
        : hasNext() ? Optional.of(peek = iterator.next()) : Optional.empty();
  }
}
