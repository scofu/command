package com.scofu.command.standard;

import com.scofu.command.Parameters;
import com.scofu.command.Result;
import com.scofu.command.model.Parameter;
import com.scofu.command.target.Arguments;
import com.scofu.command.target.Command;
import com.scofu.command.target.Escapable;
import com.scofu.command.target.Transformer;
import java.lang.reflect.Type;
import java.util.stream.Stream;

final class StringTransformer implements Transformer<String> {

  @Override
  public boolean test(Type type) {
    return type instanceof Class rawType && String.class.isAssignableFrom(rawType);
  }

  @Override
  public Result<String> transform(Command command, Parameter<String> parameter,
      Parameters parameters, Arguments arguments) {
    if (!arguments.hasNext()) {
      return Result.empty();
    }
    return parse(arguments.nextQuotable(parameter), parameter, parameters, arguments);
  }

  @Override
  public Stream<String> suggest(Command command, Parameter<String> parameter, Parameters parameters,
      Result<String> argument) {
    if (argument.hasValue()) {
      final var string = argument.get();
      if (string != null && (string.startsWith("\"") || string.contains(" "))) {
        final var split = string.endsWith(" ") ? null : string.split(" ");
        final var last = split == null ? "" : split[split.length - 1];
        if (last.endsWith("\\")) {
          if (parameter.isAnnotationPresent(Escapable.class)) {
            return Stream.of(last + "\"", last + "n", last + "u00a7");
          }
          return Stream.of(last + "\"");
        }
        return Stream.of(last + "\"", last + "\\\"");
      }
    }
    return Stream.of("\"");
  }

  private Result<String> parse(Result<String> result, Parameter<String> parameter,
      Parameters parameters, Arguments arguments) {
    if (result.hasError()) {
      return result;
    }
    if (arguments.hasNext() && !parameters.hasNext()) {
      return parse(arguments.nextQuotable(parameter).map(string -> result.get() + " " + string),
          parameter, parameters, arguments);
    }
    return result;
  }
}
