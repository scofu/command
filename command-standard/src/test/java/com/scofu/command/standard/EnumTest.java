package com.scofu.command.standard;

import static com.scofu.command.Context.simpleContext;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.inject.Inject;
import com.google.inject.Stage;
import com.scofu.app.Service;
import com.scofu.app.bootstrap.BootstrapModule;
import com.scofu.command.Dispatcher;
import com.scofu.command.model.Identified;
import com.scofu.command.text.Description;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests enums in commands.
 */
public class EnumTest extends Service {

  @Inject
  private Dispatcher dispatcher;

  @Override
  protected void configure() {
    install(new BootstrapModule(getClass().getClassLoader()));
  }

  @Test
  public void test() {
    load(Stage.PRODUCTION, this);
    final var favoriteCat = dispatcher.dispatchString(simpleContext(), "favorite cat");
    final var favoriteDog = dispatcher.dispatchString(simpleContext(), "favorite dog");
    final var suggestions = dispatcher.suggestString(simpleContext(), "favorite");
    assertEquals("My favorite animal is cat.", favoriteCat);
    assertEquals("overridden", favoriteDog);
    assertEquals(List.of("cat", "dog", "dog"), suggestions.toList());
  }

  @Identified("favorite")
  @Description("Returns my favorite animal!")
  String describeAnimal(Animal animal) {
    return "My favorite animal is %s.".formatted(animal.name().toLowerCase(Locale.ROOT));
  }

  @Identified("favorite dog")
  String overridden() {
    return "overridden";
  }

}
