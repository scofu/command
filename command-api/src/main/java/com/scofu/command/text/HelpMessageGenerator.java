package com.scofu.command.text;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.toComponent;
import static net.kyori.adventure.text.Component.translatable;

import com.google.inject.Inject;
import com.scofu.command.Context;
import com.scofu.command.ParameterException;
import com.scofu.command.model.Handle;
import com.scofu.command.model.Identifier;
import com.scofu.command.model.Node;
import com.scofu.command.model.Parameter;
import com.scofu.command.validation.Validator;
import java.lang.reflect.ParameterizedType;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

/**
 * Generates help messages.
 */
public class HelpMessageGenerator {

  private final HelpMessageConfiguration helpMessageConfiguration;
  private final Set<Validator> validators;
  private final DescriberMap describerMap;
  private final UsageGenerator usageGenerator;

  @Inject
  HelpMessageGenerator(HelpMessageConfiguration helpMessageConfiguration, Set<Validator> validators,
      DescriberMap describerMap, UsageGenerator usageGenerator) {
    this.helpMessageConfiguration = helpMessageConfiguration;
    this.validators = validators;
    this.describerMap = describerMap;
    this.usageGenerator = usageGenerator;
  }

  /**
   * Returns a component describing the given exception.
   *
   * @param parameterException the parameter exception.
   */
  public Component describeParameterError(ParameterException parameterException) {
    final var builder = text();
    final var parameter = parameterException.parameter();
    builder.append(translatable("dispatch.invoke.parameter.error",
        describeParameter(parameter).orElseGet(() -> translatable(parameter.nameOrTranslation())),
        newline()));
    describerParameterError(builder, parameterException);
    return builder.build();
  }

  /**
   * Returns a component describing the full usage a node.
   *
   * @param context              the context
   * @param identifiers          the identifiers
   * @param node                 the node
   * @param lastTestedIdentifier the last tested identifier
   * @param <T>                  the type of the input
   * @param <R>                  the type of the output
   */
  public <T, R> Component generateFullUsage(Context context,
      Iterable<? extends Identifier<?>> identifiers, Node<T, R> node,
      @Nullable Identifier<?> lastTestedIdentifier) {
    final var rootPathBuilder = text();
    final var rootParameters = text();
    rootPathBuilder.append(helpMessageConfiguration.commandPrefix()
            .map(Component::text)
            .orElse(empty())
            .color(NamedTextColor.GRAY))
        .append(StreamSupport.stream(identifiers.spliterator(), false)
            .map(Identifier::toPath)
            .map(Component::text)
            .collect(toComponent(space()))
            .color(NamedTextColor.GRAY));
    if (node.target() != null) {
      rootParameters.append(describeParameters(node).orElse(empty()).color(NamedTextColor.WHITE));
    }
    if (node.nodes(context, validators).isEmpty()) {
      return usageGenerator.generate(node, rootPathBuilder.build(), rootParameters.build());
    } else {
      final var resultBuilder = text();
      final var rootPathComponent = rootPathBuilder.build();
      if (lastTestedIdentifier != null) {
        resultBuilder.append(
            translatable("dispatch.invoke.unknown_subcommand", text(lastTestedIdentifier.toPath()),
                rootPathComponent, newline()).color(NamedTextColor.RED));
      }
      if (node.target() != null) {
        resultBuilder.append(
                usageGenerator.generate(node, rootPathComponent, rootParameters.build()))
            .append(newline());
      }
      resultBuilder.append(node.nodes(context, validators)
          .stream()
          .sorted(Comparator.comparing(entry -> entry.getKey().toPath()))
          .map(entry -> generateFullUsage(context,
              Stream.of(identifiers.spliterator(), List.of(entry.getKey()).spliterator())
                  .flatMap(i -> StreamSupport.stream(i, false))
                  .toList(), entry.getValue(), null))
          .collect(toComponent(newline())));
      return resultBuilder.build();
    }
  }

  private void describerParameterError(Builder builder, ParameterException parameterException) {
    if (parameterException.getCause() instanceof ParameterException cause) {
      describerParameterError(builder, cause);
    } else if (parameterException.getCause() != null) {
      builder.append(text("⚠ ").color(NamedTextColor.RED))
          .append(text(parameterException.getCause().getMessage()).color(NamedTextColor.RED));
    } else {
      builder.append(text("⚠ ").color(NamedTextColor.RED))
          .append(parameterException.message().color(NamedTextColor.RED));
    }
  }

  @SuppressWarnings("unchecked")
  private <T> Optional<Component> describeParameter(Parameter<T> parameter) {
    if (parameter.type() instanceof ParameterizedType parameterizedType
        && parameterizedType.getRawType() instanceof Class type) {
      if (Optional.class.isAssignableFrom(type)
          && parameterizedType.getActualTypeArguments().length > 0) {
        final var typeArgument = parameterizedType.getActualTypeArguments()[0];
        return describerMap.get(typeArgument)
            .flatMap(describer -> ((Describer<T>) describer).describe(parameter));
      }
    }
    return describerMap.get(parameter.type())
        .flatMap(describer -> ((Describer<T>) describer).describe(parameter));
  }

  @SuppressWarnings("unchecked")
  private <T> Optional<Component> describeParameterWithBrackets(Parameter<T> parameter) {
    if (parameter.type() instanceof ParameterizedType parameterizedType
        && parameterizedType.getRawType() instanceof Class type) {
      if (Optional.class.isAssignableFrom(type)
          && parameterizedType.getActualTypeArguments().length > 0) {
        final var typeArgument = parameterizedType.getActualTypeArguments()[0];
        return describerMap.get(typeArgument)
            .flatMap(describer -> ((Describer<T>) describer).describe(parameter))
            .map(component -> translatable("parameter.optional.format",
                translatable("parameter.optional.prefix"), component,
                translatable("parameter.optional.suffix")));
      }
    }
    return describerMap.get(parameter.type())
        .flatMap(describer -> ((Describer<T>) describer).describe(parameter))
        .map(component -> translatable("parameter.required.format",
            translatable("parameter.required.prefix"), component,
            translatable("parameter.required.suffix")));
  }

  private <T, R> Optional<Component> describeParameters(Node<T, R> node) {
    return Optional.ofNullable(node.handle()).map(Handle::parameters).map(this::describeParameters);
  }

  private Component describeParameters(List<Parameter<?>> parameters) {
    Component component = null;
    for (Parameter<?> parameter : parameters) {
      final var parameterComponent = describeParameterWithBrackets(parameter).orElse(null);
      if (parameterComponent == null) {
        continue;
      }
      if (component == null) {
        component = space().append(parameterComponent);
      } else {
        component = component.append(space()).append(parameterComponent);
      }
    }
    return component;
  }

}
