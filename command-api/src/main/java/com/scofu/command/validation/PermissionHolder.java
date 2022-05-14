package com.scofu.command.validation;

/** Expansion type that identifies something that holds permissions. */
public interface PermissionHolder {

  /**
   * Returns whether this permission holder has the given permission or not.
   *
   * @param permission the permission
   */
  boolean hasPermission(String permission);
}
