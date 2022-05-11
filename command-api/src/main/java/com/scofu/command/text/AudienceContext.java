package com.scofu.command.text;

import static com.scofu.text.ContextualizedComponent.error;
import static net.kyori.adventure.text.Component.newline;

import com.scofu.command.Context;
import com.scofu.command.DispatchHandleUnvalidatedException;
import com.scofu.command.ParameterException;
import com.scofu.command.model.Expansion;
import com.scofu.command.model.Identifier;
import com.scofu.command.model.Node;
import com.scofu.text.Theme;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.Nullable;

/**
 * An audience based context.
 */
public class AudienceContext implements Context {

  private final Map<Identifier<?>, Expansion<?>> expansions;
  private final Audience audience;
  private final Locale locale;
  private final HelpMessageGenerator helpMessageGenerator;
  private final Theme theme;

  /**
   * Constructs a new audience based context.
   *
   * @param audience             the audience
   * @param locale               the locale
   * @param helpMessageGenerator the help message generator
   * @param theme                the theme
   */
  public AudienceContext(Audience audience, Locale locale,
      HelpMessageGenerator helpMessageGenerator, Theme theme) {
    this.audience = audience;
    this.locale = locale;
    this.helpMessageGenerator = helpMessageGenerator;
    this.theme = theme;
    this.expansions = new ConcurrentHashMap<>();
  }

  @Override
  public Locale locale() {
    return locale;
  }

  @Override
  public Theme theme() {
    return theme;
  }

  @Override
  public <T, R> R onDispatchResolveError(Iterable<? extends Identifier<?>> identifiers, T argument,
      Throwable throwable) {
    if (throwable instanceof DispatchHandleUnvalidatedException unvalidatedException) {
      error().text("dispatch.resolve.unvalidated")
          .prefixed()
          .renderTo(theme, audience::sendMessage);
      return null;
    }

    error().text("dispatch.resolve.error").prefixed().renderTo(theme, audience::sendMessage);
    throwable.printStackTrace();
    return null;
  }

  @Override
  public <T, R> R onDispatchNoTarget(Iterable<? extends Identifier<?>> identifiers, Node<T, R> node,
      T argument, @Nullable Identifier<?> lastTestedIdentifier) {
    audience.sendMessage(newline().append(
            helpMessageGenerator.generateFullUsage(this, identifiers, node, lastTestedIdentifier))
        .append(newline()));
    return null;
  }

  @Override
  public <T, R> R onDispatchNoHandle(Iterable<? extends Identifier<?>> identifiers, Node<T, R> node,
      T argument) {
    throw new IllegalStateException("No handle for node: " + node);
  }

  @Override
  public <R, T> R onDispatchInvokeError(Iterable<? extends Identifier<?>> identifiers,
      Node<T, R> node, T argument, Throwable throwable) {
    if (throwable instanceof ParameterException parameterException) {
      audience.sendMessage(
          newline().append(helpMessageGenerator.generateFullUsage(this, identifiers, node, null))
              .append(newline())
              .append(helpMessageGenerator.describeParameterError(parameterException, theme))
              .append(newline()));
      return null;
    }
    error().text("dispatch.invoke.error").prefixed().renderTo(theme, audience::sendMessage);
    throwable.printStackTrace();
    return null;
  }

  @Override
  public Map<Identifier<?>, Expansion<?>> expansions() {
    return expansions;
  }
}
