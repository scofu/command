package com.scofu.command.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import com.scofu.command.Parameters;
import com.scofu.command.model.Parameter;
import java.util.Iterator;

/** Real parameters. */
public class RealParameters implements Parameters {

  private final Iterator<Parameter<?>> iterator;

  private RealParameters(Iterator<Parameter<?>> iterator) {
    this.iterator = iterator;
  }

  /**
   * Creates and returns a new real parameters.
   *
   * @param iterator the iterator
   */
  public static RealParameters newRealParameters(Iterator<Parameter<?>> iterator) {
    checkNotNull(iterator, "iterator");
    return new RealParameters(iterator);
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public Parameter<?> next() {
    return iterator.next();
  }
}
