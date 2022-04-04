package com.scofu.command.text;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.translatable;

import com.scofu.command.Context;
import com.scofu.command.DispatchHandleUnvalidatedException;
import com.scofu.command.ParameterException;
import com.scofu.command.model.Expansion;
import com.scofu.command.model.Identifier;
import com.scofu.command.model.Node;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

/**
 * An audience based context.
 */
public class AudienceContext implements Context {

  private final Map<Identifier<?>, Expansion<?>> expansions;
  private final Audience audience;
  private final Locale locale;
  private final HelpMessageGenerator helpMessageGenerator;

  /**
   * Constructs a new audience based context.
   *
   * @param audience             the audience
   * @param locale               the locale
   * @param helpMessageGenerator the help message generator
   */
  public AudienceContext(Audience audience, Locale locale,
      HelpMessageGenerator helpMessageGenerator) {
    this.audience = audience;
    this.locale = locale;
    this.helpMessageGenerator = helpMessageGenerator;
    this.expansions = new ConcurrentHashMap<>();
  }

  @Override
  public Locale locale() {
    return locale;
  }

  @Override
  public <T, R> R onDispatchResolveError(Iterable<? extends Identifier<?>> identifiers, T argument,
      Throwable throwable) {
    if (throwable instanceof DispatchHandleUnvalidatedException unvalidatedException) {
      audience.sendMessage(translatable("dispatch.resolve.unvalidated").color(NamedTextColor.RED));
      return null;
    }

    audience.sendMessage(translatable("dispatch.resolve.error").color(NamedTextColor.RED));
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
              .append(helpMessageGenerator.describeParameterError(parameterException))
              .append(newline()));
      return null;
    }
    audience.sendMessage(translatable("dispatch.invoke.error").color(NamedTextColor.RED));
    throwable.printStackTrace();
    return null;
  }

  @Override
  public Map<Identifier<?>, Expansion<?>> expansions() {
    return expansions;
  }
}
