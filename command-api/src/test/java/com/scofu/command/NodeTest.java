package com.scofu.command;

import static com.scofu.command.Context.simpleContext;
import static com.scofu.command.model.Node.node;
import static com.scofu.common.Identifier.identifier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.scofu.command.validation.Permission;
import com.scofu.command.validation.PermissionValidator;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Tests {@link com.scofu.command.model.Node}. */
public class NodeTest {

  @Test
  public void testIdentifiers() {
    final var test = node().identifier("test").build();
    assertEquals(test.identifiers().size(), 1);
    assertEquals(test.identifiers().iterator().next(), identifier("test"));
  }

  @Test
  public void testChild() throws Throwable {
    final var test = node().identifier("test").child().identifier("child").endNode().build();
    final var childResult =
        test.validateDirectChildByIdentifier(simpleContext(), Set.of(), true, identifier("child"));
    assertFalse(childResult.hasError());
    assertSame(childResult.get(), test.nodes().get(identifier("child")));
  }

  @Test
  public void testValidateChildNoPermission() throws Throwable {
    final var test =
        node()
            .identifier("test")
            .child()
            .identifier("child")
            .map(Permission.PERMISSION_IDENTIFIER)
            .to("test.permission")
            .endNode()
            .build();
    final var context = simpleContext().map(Permission.HOLDER_IDENTIFIER).to(permission -> false);
    final var childResult =
        test.validateDirectChildByIdentifier(
            context, Set.of(new PermissionValidator()), true, identifier("child"));
    assertTrue(childResult.hasError());
    assertTrue(childResult.theError() instanceof DispatchHandleUnvalidatedException);
  }

  @Test
  public void testValidateChildWithPermission() throws Throwable {
    final var node =
        node()
            .identifier("test")
            .child()
            .identifier("child")
            .map(Permission.PERMISSION_IDENTIFIER)
            .to("test.permission")
            .endNode()
            .build();
    final var context =
        simpleContext()
            .map(Permission.HOLDER_IDENTIFIER)
            .to(permission -> permission.equals("test.permission"));
    final var childResult =
        node.validateDirectChildByIdentifier(
            context, Set.of(new PermissionValidator()), true, identifier("child"));
    assertFalse(childResult.hasError());
    assertSame(childResult.get(), node.nodes().get(identifier("child")));
  }
}
