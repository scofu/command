package com.scofu.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.inject.Inject;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import com.scofu.app.Service;
import com.scofu.app.bootstrap.BootstrapModule;
import com.scofu.command.model.Identified;
import com.scofu.command.target.Escapable;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link com.scofu.command.target.Escapable} in commands.
 */
public class EscapableTest extends Service {

  @Inject
  private Dispatcher dispatcher;

  @Override
  protected void configure() {
    install(new BootstrapModule(getClass().getClassLoader()));
    bindFeature(StringTransformer.class).in(Scopes.SINGLETON);
  }

  @Test
  public void test() {
    load(Stage.PRODUCTION, this);
    final var greeting = dispatcher.dispatchString(Context.simple(), "greet \"hello, \\u0025s!\"");
    assertEquals("hello, world!", greeting);
  }

  @Identified("greet")
  String format(@Escapable String greeting) {
    return greeting.formatted("world");
  }

}
