package com.scofu.command.standard;

import static com.scofu.command.Context.simpleContext;
import static com.scofu.command.model.Identifier.identifier;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.inject.Inject;
import com.google.inject.Stage;
import com.scofu.app.Service;
import com.scofu.app.bootstrap.BootstrapModule;
import com.scofu.command.Dispatcher;
import com.scofu.command.model.Expansion;
import com.scofu.command.model.Identified;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Optional} in commands.
 */
public class ExpansionTest extends Service {

  @Inject
  private Dispatcher dispatcher;

  @Override
  protected void configure() {
    install(new BootstrapModule(getClass().getClassLoader()));
  }

  @Test
  public void test() {
    load(Stage.PRODUCTION, this);
    final var context = simpleContext();
    context.map(identifier("b")).to(34);
    final var one = dispatcher.dispatchString(context, "subtract 35");
    context.map(identifier("b")).toNothing();
    final var three = dispatcher.dispatchString(context, "subtract 3");
    assertEquals(1, one);
    assertEquals(3, three);
  }

  @Identified("subtract")
  int subtract(int a, @Identified("b") Expansion<Integer> b) {
    return a - b.get().orElse(0);
  }

}
