package com.scofu.command.model;

import com.scofu.command.target.FutureTarget;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Methods annotated with this can be discovered by {@link TransformingNodeDiscoverer}.
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(MultiIdentified.class)
public @interface Identified {

  Identifier<Method> METHOD_IDENTIFIER = Identifier.identifier("discovered_method");

  /**
   * Returns the identifier.
   */
  String value();

  /**
   * Returns the alises.
   */
  String[] aliases() default {};

  /**
   * Returns whether this node is futile, ie it has no target.
   */
  boolean futile() default false;

  /**
   * Returns whether this node should be transformed with a {@link FutureTarget} or not.
   */
  boolean async() default false;
}
