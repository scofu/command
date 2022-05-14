package com.scofu.command.standard;

import static com.scofu.command.Context.simpleContext;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.inject.Inject;
import com.google.inject.Stage;
import com.scofu.app.Service;
import com.scofu.app.bootstrap.BootstrapModule;
import com.scofu.command.Dispatcher;
import com.scofu.command.model.Identified;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Tests {@link java.util.Optional} in commands. */
public class OptionalTest extends Service {

  @Inject private Dispatcher dispatcher;

  @Override
  protected void configure() {
    install(new BootstrapModule(getClass().getClassLoader()));
  }

  @Test
  public void test() {
    load(Stage.PRODUCTION, this);
    final var sixtyNine = dispatcher.dispatchString(simpleContext(), "add 35 34");
    final var three = dispatcher.dispatchString(simpleContext(), "add 3");
    assertEquals(69, sixtyNine);
    assertEquals(3, three);
  }

  @Identified("add")
  int add(int a, Optional<Integer> b) {
    return a + b.orElse(0);
  }
}
