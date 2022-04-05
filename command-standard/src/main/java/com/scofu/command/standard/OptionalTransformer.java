package com.scofu.command.standard;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

import com.google.inject.Inject;
import com.scofu.command.ParameterException;
import com.scofu.command.Parameters;
import com.scofu.command.Result;
import com.scofu.command.model.Parameter;
import com.scofu.command.target.Arguments;
import com.scofu.command.target.Command;
import com.scofu.command.target.Transformer;
import com.scofu.command.target.TransformerMap;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.Stream;

final class OptionalTransformer implements Transformer<Optional> {

  private final TransformerMap transformerMap;

  @Inject
  OptionalTransformer(TransformerMap transformerMap) {
    this.transformerMap = transformerMap;
  }

  @Override
  public boolean test(Type type) {
    return type instanceof ParameterizedType parameterizedType
        && parameterizedType.getRawType() instanceof Class rawType
        && Optional.class.isAssignableFrom(rawType);
  }

  @Override
  public Result<Optional> transform(Command command, Parameter<Optional> parameter,
      Parameters parameters, Arguments arguments) {
    final var argumentType = parameter.type() instanceof ParameterizedType parameterizedType
        && parameterizedType.getActualTypeArguments().length > 0
        ? parameterizedType.getActualTypeArguments()[0] : null;

    if (argumentType == null) {
      return Result.error(
          new ParameterException(translatable("optional.transform.missing_type_argument"),
              parameter));
    }

    final var transformer = transformerMap.get(argumentType)
        .orElseThrow(() -> new ParameterException(
            translatable("transformer.parameter.missing_transformer",
                text(argumentType.toString())), parameter));

    final var result = transformer.transform(command, (Parameter) parameter, parameters, arguments);
    return result.map(Optional::ofNullable);
  }

  @Override
  public Stream<String> suggest(Command command, Parameter<Optional> parameter,
      Parameters parameters, Result<String> argument) {
    final var argumentType = parameter.type() instanceof ParameterizedType parameterizedType
        && parameterizedType.getActualTypeArguments().length > 0
        ? parameterizedType.getActualTypeArguments()[0] : null;

    if (argumentType == null) {
      throw new ParameterException(translatable("optional.transform.missing_type_argument"),
          parameter);
    }

    final var transformer = transformerMap.get(argumentType)
        .orElseThrow(() -> new ParameterException(
            translatable("transformer.parameter.missing_transformer",
                text(argumentType.toString())), parameter));

    return transformer.suggest(command, (Parameter) parameter, parameters, argument);
  }
}
