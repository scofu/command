package com.scofu.command.standard;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

import com.scofu.command.ParameterArgumentException;
import com.scofu.command.Parameters;
import com.scofu.command.Result;
import com.scofu.command.model.Parameter;
import com.scofu.command.target.Arguments;
import com.scofu.command.target.Command;
import com.scofu.command.target.Transformer;
import java.lang.reflect.Type;
import java.util.stream.Stream;

final class IntegerTransformer implements Transformer<Integer> {

  @Override
  public boolean test(Type type) {
    return type instanceof Class rawType && (Integer.class.isAssignableFrom(rawType)
        || int.class.isAssignableFrom(rawType));
  }

  @Override
  public Result<Integer> transform(Command command, Parameter<Integer> parameter,
      Parameters parameters, Arguments arguments) {
    if (!arguments.hasNext()) {
      return Result.empty();
    }
    return arguments.nextQuotable(parameter).flatMap(string -> parseInteger(string, parameter));
  }

  @Override
  public Stream<String> suggest(Command command, Parameter<Integer> parameter,
      Parameters parameters, Result<String> argument) {
    if (argument.hasError()) {
      return Stream.of("\"");
    }
    if (argument.hasValue()) {
      final var string = argument.get();
      if (string != null) {
        return Stream.of(string + "0", string + "5", string + "00", string + "50");
      }
    }
    return Stream.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "0");
  }

  private Result<Integer> parseInteger(String string, Parameter<Integer> parameter) {
    try {
      return Result.value(Integer.parseInt(string));
    } catch (NumberFormatException e) {
      return Result.error(
          new ParameterArgumentException(translatable("integer.transform.invalid", text(string)),
              parameter));
    }
  }
}
