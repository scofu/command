package com.scofu.command.target;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

import com.google.inject.Inject;
import com.scofu.command.ParameterArgumentException;
import com.scofu.command.ParameterException;
import com.scofu.command.internal.RealArguments;
import com.scofu.command.internal.RealParameters;
import com.scofu.command.model.Parameter;
import java.util.List;

/**
 * A target that automatically transforms strings to objects using transformers.
 */
public class TransformingTarget implements Target<List<String>, Object[]> {

  private final TransformerMap transformerMap;

  @Inject
  TransformingTarget(TransformerMap transformerMap) {
    this.transformerMap = transformerMap;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object[] invoke(Command command, List<String> argument) throws Throwable {
    final var arguments = RealArguments.newRealArguments(
        (argument == null ? List.<String>of() : argument).iterator());
    final var parameters = RealParameters.newRealParameters(
        command.node().handle().parameters().iterator());
    final var objects = new Object[command.node().handle().parameters().size()];
    var index = 0;
    while (parameters.hasNext()) {
      final var parameter = parameters.next();
      final var transformer = transformerMap.get(parameter.type())
          .orElseThrow(() -> new ParameterException(
              translatable("transformer.parameter.missing_transformer",
                  text(parameter.type().toString())), parameter));
      final var result = transformer.transform(command, (Parameter) parameter, parameters,
          arguments);
      if (result.hasError()) {
        throw new ParameterException(
            translatable("transformer.parameter.error", text(parameter.type().toString())),
            result.error(), parameter);
      }
      final var object = result.get();
      if (object == null) {
        throw new ParameterArgumentException(translatable("transformer.parameter.missing_argument"),
            parameter);
      }
      objects[index++] = object;
    }
    if (arguments.hasNext()) {
      System.out.println("Unused arguments: " + arguments.remaining().toList());
    }
    return objects;
  }
}
