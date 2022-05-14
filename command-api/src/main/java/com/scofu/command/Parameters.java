package com.scofu.command;

import com.scofu.command.model.Parameter;

/** Iterates over parameters. */
public interface Parameters {

  /** Returns whether another parameter is present or not. */
  boolean hasNext();

  /** Returns the next parameter. */
  Parameter<?> next();
}
