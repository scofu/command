package com.scofu.command.validation;

import static com.scofu.common.Identifier.identifier;

import com.scofu.common.Identifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Nodes discovered annotated with this will automatically have a permission attached. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

  Identifier<PermissionHolder> HOLDER_IDENTIFIER = identifier("permission_holder");
  Identifier<String> PERMISSION_IDENTIFIER = identifier("permission");

  /** Returns the permission. */
  String value();
}
