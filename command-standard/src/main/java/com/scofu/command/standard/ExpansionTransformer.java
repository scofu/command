package com.scofu.command.standard;

import static com.scofu.command.model.Identifier.identifier;

import com.scofu.command.Parameters;
import com.scofu.command.Result;
import com.scofu.command.model.Expansion;
import com.scofu.command.model.Identified;
import com.scofu.command.model.Parameter;
import com.scofu.command.target.Arguments;
import com.scofu.command.target.Command;
import com.scofu.command.target.Transformer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

final class ExpansionTransformer implements Transformer<Expansion> {

  @Override
  public boolean test(Type type) {
    return type instanceof ParameterizedType parameterizedType
        && parameterizedType.getRawType() instanceof Class rawType
        && Expansion.class.isAssignableFrom(rawType);
  }

  @Override
  public Result<Expansion> transform(Command command, Parameter<Expansion> parameter,
      Parameters parameters, Arguments arguments) {
    final var identifier = identifier(Optional.ofNullable(parameter.getAnnotation(Identified.class))
        .map(Identified::value)
        .orElseGet(parameter::nameOrTranslation));
    return Result.value(Optional.ofNullable(command.context().expansions().get(identifier))
        .orElseGet(() -> Expansion.empty()));
  }

  @Override
  public boolean ignoresSuggestionsForParameter(Parameter<Expansion> parameter) {
    return true;
  }
}
