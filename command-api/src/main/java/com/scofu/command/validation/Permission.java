package com.scofu.command.validation;

import com.scofu.command.model.Identifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Nodes discovered annotated with this will automatically have a permission attached.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

  Identifier<PermissionHolder> HOLDER_IDENTIFIER = Identifier.of("permission_holder");
  Identifier<String> PERMISSION_IDENTIFIER = Identifier.of("permission");

  /**
   * Returns the permission.
   */
  String value();
}
