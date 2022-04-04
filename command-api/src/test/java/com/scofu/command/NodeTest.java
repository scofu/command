package com.scofu.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.scofu.command.model.Identifier;
import com.scofu.command.model.Node;
import com.scofu.command.validation.Permission;
import com.scofu.command.validation.PermissionValidator;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link com.scofu.command.model.Node}.
 */
public class NodeTest {

  @Test
  public void testIdentifiers() {
    final var test = Node.builder(Identifier.of("test")).build();
    assertEquals(test.identifiers().size(), 1);
    assertEquals(test.identifiers().iterator().next(), Identifier.of("test"));
  }

  @Test
  public void testChild() throws Throwable {
    final var test = Node.builder(Identifier.of("test")).withChild(Identifier.of("child"))
        .endChild().build();
    final var childResult = test.validateDirectChildByIdentifier(Context.simple(), Set.of(), true,
        Identifier.of("child"));
    assertFalse(childResult.hasError());
    assertSame(childResult.get(), test.nodes().get(Identifier.of("child")));
  }

  @Test
  public void testValidateChildNoPermission() throws Throwable {
    final var test = Node.builder(Identifier.of("test")).withChild(Identifier.of("child"))
        .map(Permission.PERMISSION_IDENTIFIER).to("test.permission").endChild().build();
    final var context = Context.simple().map(Permission.HOLDER_IDENTIFIER).to(permission -> false);
    final var childResult = test.validateDirectChildByIdentifier(context,
        Set.of(new PermissionValidator()), true, Identifier.of("child"));
    assertTrue(childResult.hasError());
    assertTrue(childResult.error() instanceof DispatchHandleUnvalidatedException);
  }

  @Test
  public void testValidateChildWithPermission() throws Throwable {
    final var node = Node.builder(Identifier.of("test")).withChild(Identifier.of("child"))
        .map(Permission.PERMISSION_IDENTIFIER).to("test.permission").endChild().build();
    final var context = Context.simple().map(Permission.HOLDER_IDENTIFIER)
        .to(permission -> permission.equals("test.permission"));
    final var childResult = node.validateDirectChildByIdentifier(context,
        Set.of(new PermissionValidator()), true, Identifier.of("child"));
    assertFalse(childResult.hasError());
    assertSame(childResult.get(), node.nodes().get(Identifier.of("child")));
  }

}
