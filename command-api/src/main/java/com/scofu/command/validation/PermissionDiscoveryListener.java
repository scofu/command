package com.scofu.command.validation;

import com.scofu.command.model.DiscoveryListener;
import com.scofu.command.model.Identified;
import com.scofu.command.model.Node;

/** Attaches permissions to discovered nodes annotated with {@link Permission}. */
public class PermissionDiscoveryListener implements DiscoveryListener {

  @Override
  public <T, R> void onDiscovery(Node<T, R> node, boolean root) {
    System.out.println("ATTACHING PERMISSION: " + node.identifiers());
    node.expand(Identified.METHOD_IDENTIFIER)
        .filter(method -> method.isAnnotationPresent(Permission.class))
        .map(method -> method.getAnnotation(Permission.class))
        .map(Permission::value)
        .ifPresent(permission -> node.map(Permission.PERMISSION_IDENTIFIER).to(permission));
  }
}
