package com.scofu.command.standard;

import com.scofu.command.Context;
import com.scofu.command.Parameters;
import com.scofu.command.Result;
import com.scofu.command.model.Parameter;
import com.scofu.command.target.Arguments;
import com.scofu.command.target.Command;
import com.scofu.command.target.Transformer;
import java.lang.reflect.Type;

final class ContextTransformer implements Transformer<Context> {

  @Override
  public boolean test(Type type) {
    return type instanceof Class rawType && Context.class.isAssignableFrom(rawType);
  }

  @Override
  public Result<Context> transform(Command command, Parameter<Context> parameter,
      Parameters parameters, Arguments arguments) {
    return Result.value(command.context());
  }

  @Override
  public boolean ignoresSuggestionsForParameter(Parameter<Context> parameter) {
    return true;
  }
}
