package com.scofu.command.target;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

import com.google.inject.Inject;
import com.scofu.command.ParameterException;
import com.scofu.command.Result;
import com.scofu.command.internal.RealArguments;
import com.scofu.command.internal.RealParameters;
import com.scofu.command.model.Parameter;
import java.util.List;
import java.util.stream.Stream;

/** A target that automatically transforms strings to objects using transformers. */
public class TransformingSuggester implements Suggester<List<String>> {

  private final TransformerMap transformerMap;

  @Inject
  TransformingSuggester(TransformerMap transformerMap) {
    this.transformerMap = transformerMap;
  }

  @SuppressWarnings({"unchecked", "ConstantConditions", "rawtypes"})
  @Override
  public Stream<String> suggest(Command command, List<String> argument) {
    final var arguments =
        RealArguments.newRealArguments(
            (argument == null ? List.<String>of() : argument).iterator());
    final var parameters =
        RealParameters.newRealParameters(command.node().handle().parameters().iterator());
    while (parameters.hasNext()) {
      final var parameter = parameters.next();
      final var transformer =
          transformerMap
              .get(parameter.type())
              .orElseThrow(
                  () ->
                      new ParameterException(
                          translatable(
                              "transformer.parameter.missing_transformer",
                              text(parameter.type().toString())),
                          parameter));
      if (transformer.ignoresSuggestionsForParameter((Parameter) parameter)) {
        continue;
      }

      var actualArgument = Result.<String>empty();
      if (arguments.hasNext()) {
        actualArgument = arguments.nextQuotable(parameter);
        if (parameters.hasNext() && arguments.hasNextOrEmptyNext()) {
          continue;
        }
      }
      return transformer.suggest(command, (Parameter) parameter, parameters, actualArgument);
    }
    if (arguments.hasNext()) {
      System.out.println("Unused arguments: " + arguments.remaining().toList());
    }
    return Stream.empty();
  }
}
