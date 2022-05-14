package com.scofu.command.validation;

import com.scofu.command.Context;
import com.scofu.command.model.Node;

/** A validator that validates permissions. */
public class PermissionValidator implements Validator {

  public static final boolean TRUE_BY_DEFAULT = true;

  @Override
  public <T, R> boolean validate(Context context, Node<T, R> node) {
    return context
        .expand(Permission.HOLDER_IDENTIFIER)
        .flatMap(
            permissionHolder ->
                node.expand(Permission.PERMISSION_IDENTIFIER).map(permissionHolder::hasPermission))
        .orElse(TRUE_BY_DEFAULT);
  }
}
