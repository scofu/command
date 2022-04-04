package com.scofu.command.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to stack discoverable annotations.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Discoverables {

  /**
   * Returns the discoverable annotations.
   */
  Discoverable[] value();

}
