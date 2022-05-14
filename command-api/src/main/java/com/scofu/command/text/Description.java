package com.scofu.command.text;

import static com.scofu.command.model.Identifier.identifier;

import com.scofu.command.model.Identifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Nodes discovered annotated with this will automatically have a description attached. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {

  Identifier<String> DESCRIPTION_IDENTIFIER = identifier("description");

  /** Returns the translation key or raw description. */
  String value();

  /** Returns whether this description is a translation key or not. */
  boolean notTranslated() default false;
}
