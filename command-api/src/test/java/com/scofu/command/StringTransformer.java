package com.scofu.command;

import com.google.inject.internal.MoreTypes;
import com.scofu.command.model.Parameter;
import com.scofu.command.target.Arguments;
import com.scofu.command.target.Command;
import com.scofu.command.target.Transformer;
import java.lang.reflect.Type;

final class StringTransformer implements Transformer<String> {

  @Override
  public boolean test(Type type) {
    return String.class.isAssignableFrom(MoreTypes.getRawType(type));
  }

  @Override
  public Result<String> transform(
      Command command, Parameter<String> parameter, Parameters parameters, Arguments arguments) {
    return arguments.nextQuotable(parameter);
  }
}
