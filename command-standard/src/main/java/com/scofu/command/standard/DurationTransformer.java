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
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Stream;

final class DurationTransformer implements Transformer<Duration> {

  private static final Pattern PATTERN = Pattern.compile("([0-9]+)([smhdwy])");

  @Override
  public boolean test(Type type) {
    return type instanceof Class rawType && Duration.class.isAssignableFrom(rawType);
  }

  @Override
  public Result<Duration> transform(Command command, Parameter<Duration> parameter,
      Parameters parameters, Arguments arguments) {
    if (!arguments.hasNext()) {
      return Result.empty();
    }
    return arguments.nextQuotable(parameter).flatMap(string -> parseDuration(string, parameter));
  }

  @Override
  public Stream<String> suggest(Command command, Parameter<Duration> parameter,
      Parameters parameters, Result<String> argument) {
    if (argument.hasError()) {
      return Stream.of("\"");
    }
    return Stream.of("infinite", "permanent", "s", "m", "h", "d", "w", "y");
  }

  private Result<Duration> parseDuration(String string, Parameter<Duration> parameter) {
    if (string.equalsIgnoreCase("infinity") || string.equalsIgnoreCase("infinite")
        || string.equalsIgnoreCase("permanent")) {
      return Result.value(Duration.ofNanos(Long.MAX_VALUE));
    }
    string = string.toLowerCase(Locale.ENGLISH);
    final var matcher = PATTERN.matcher(string);
    Instant instant = null;
    while (matcher.find()) {
      if (instant == null) {
        instant = Instant.EPOCH;
      }
      final var amount = Integer.parseInt(matcher.group(1));
      final var type = matcher.group(2);
      switch (type) {
        case "s" -> instant = instant.plus(Duration.ofSeconds(amount));
        case "m" -> instant = instant.plus(Duration.ofMinutes(amount));
        case "h" -> instant = instant.plus(Duration.ofHours(amount));
        case "d" -> instant = instant.plus(Duration.ofDays(amount));
        case "w" -> instant = instant.plus(Duration.ofDays((long) amount * 7));
        case "y" -> instant = instant.plus(Duration.ofDays((long) (amount * 365.242199)));
        default -> {
        }
      }
    }
    if (instant == null) {
      return Result.error(
          new ParameterArgumentException(translatable("%s is not a valid duration.", text(string)),
              parameter));
    }
    return Result.value(Duration.ofMillis(instant.toEpochMilli()));
  }
}
