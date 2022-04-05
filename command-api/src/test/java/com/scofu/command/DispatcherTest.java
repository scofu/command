package com.scofu.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.inject.Inject;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import com.scofu.app.Service;
import com.scofu.app.bootstrap.BootstrapModule;
import com.scofu.command.model.Identified;
import com.scofu.command.model.Identifier;
import com.scofu.command.model.Node;
import com.scofu.command.target.Target;
import com.scofu.common.inject.Feature;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link Dispatcher}.
 */
public class DispatcherTest extends Service {

  @Inject
  private Dispatcher dispatcher;

  @Override
  protected void configure() {
    install(new BootstrapModule(getClass().getClassLoader()));
    bindFeature(StringTransformer.class).in(Scopes.SINGLETON);

    // discovered
    bindFeatureInstance(new Feature() {
      @Identified(value = "test", futile = true)
      void root() {
      }

      @Identified(value = "test uppercase")
      String uppercase(String message) {
        return message.toUpperCase(Locale.ROOT);
      }
    });

    // manual
    final var dispatcherProvider = getProvider(Dispatcher.class);
    bindFeatureInstance(new Feature() {
      @Override
      public void enable() {
        final var target = (Target<List<String>, String>) (command, argument) -> argument.get(0)
            .toLowerCase(Locale.ROOT);
        final var node = Node.builder(Identifier.of("lowercase"))
            .withHandle()
            .withParameter()
            .withName("message")
            .withType(String.class)
            .endParameter()
            .endHandle()
            .withTarget(target)
            .build();
        dispatcherProvider.get().nodes().get(Identifier.of("test")).register(node);
      }
    });
  }

  @Test()
  public void test() {
    load(Stage.PRODUCTION, this);

    final var toUpper = (Function<String, String>) (message ->
        dispatcher.dispatchString(Context.simple(), "test uppercase " + message));
    final var toLower = (Function<String, String>) (message ->
        dispatcher.dispatchString(Context.simple(), "test lowercase " + message));
    final var toUpperThenToLower = toUpper.andThen(toLower);

    assertEquals("HELLO", toUpper.apply("hello"));
    assertEquals("hello", toLower.apply("HELLO"));
    assertEquals("hello", toUpperThenToLower.apply("hello"));
  }

}
