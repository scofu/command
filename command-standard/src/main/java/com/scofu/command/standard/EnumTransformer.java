package com.scofu.command.standard;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.scofu.command.ParameterArgumentException;
import com.scofu.command.Parameters;
import com.scofu.command.Result;
import com.scofu.command.model.Parameter;
import com.scofu.command.target.Arguments;
import com.scofu.command.target.Command;
import com.scofu.command.target.Transformer;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.TranslationRegistry;

final class EnumTransformer implements Transformer<Enum> {

  public static final int MAX_IN_ERROR = 10;

  private final LoadingCache<Class<Enum>, Enum[]> enums;
  private final LoadingCache<Class<Enum>, Component> errors;
  private final TranslationRegistry translationRegistry;

  @Inject
  EnumTransformer(TranslationRegistry translationRegistry) {
    this.translationRegistry = translationRegistry;
    this.enums = CacheBuilder.newBuilder()
        .expireAfterAccess(1, TimeUnit.HOURS)
        .build(new CacheLoader<>() {
          @Override
          public Enum[] load(Class<Enum> key) throws Exception {
            return key.getEnumConstants();
          }
        });
    this.errors = CacheBuilder.newBuilder()
        .expireAfterAccess(1, TimeUnit.HOURS)
        .build(new CacheLoader<>() {
          @Override
          public Component load(Class<Enum> key) throws Exception {
            final var enums = EnumTransformer.this.enums.getUnchecked(key);
            if (enums.length <= 1) {
              return enums.length == 0 ? empty() : toComponent(enums[0]);
            }
            var joined = Stream.of(enums)
                .limit(Math.min(MAX_IN_ERROR - 1, enums.length - 1))
                .map(EnumTransformer.this::toComponent)
                .collect(Component.toComponent(text(", ")))
                .append(space());
            var last = toComponent(enums.length > (MAX_IN_ERROR - 1) ? enums[MAX_IN_ERROR - 1]
                : enums[enums.length - 1]);
            var values = joined.append(translatable("enum.transform.invalid.or"))
                .append(space())
                .append(last);
            if (enums.length > (MAX_IN_ERROR - 1)) {
              return values.append(
                  translatable("enum.transform.invalid.remaining", text(MAX_IN_ERROR),
                      text(enums.length)));
            }
            return values;
          }
        });
  }

  private Component toComponent(Enum<?> constant) {
    return constant instanceof TranslatedEnum translatedEnum ? translatable(
        translatedEnum.translation()) : text(constant.name().toLowerCase(Locale.ROOT));
  }

  @Override
  public boolean test(Type type) {
    return type instanceof Class rawType && rawType.isEnum();
  }

  @Override
  public Result<Enum> transform(Command command, Parameter<Enum> parameter, Parameters parameters,
      Arguments arguments) {
    if (!arguments.hasNext()) {
      return Result.empty();
    }
    return arguments.nextQuotable(parameter)
        .flatMap(argument -> parseEnum(command.context().locale(), argument, parameter));
  }

  @Override
  public Stream<String> suggest(Command command, Parameter<Enum> parameter, Parameters parameters,
      Result<String> argument) {
    if (argument.hasError()) {
      return Stream.of("\"");
    }
    final var enumClass = (Class<Enum>) parameter.type();
    return Stream.of(enums.getUnchecked(enumClass))
        .map(constant -> constant instanceof TranslatedEnum translatedEnum
            ? translationRegistry.translate(translatedEnum.translation(),
            command.context().locale()).format(null) : constant.name())
        .map(String::toLowerCase);
  }

  private Result<Enum> parseEnum(Locale locale, String argument, Parameter<Enum> parameter) {
    final var enumClass = (Class<Enum>) parameter.type();
    if (TranslatedEnum.class.isAssignableFrom(enumClass)) {
      for (var constant : enumClass.getEnumConstants()) {
        if (translationRegistry.translate(((TranslatedEnum) constant).translation(), locale)
            .format(null)
            .equals(argument)) {
          return Result.value(constant);
        }
      }
    } else {
      try {
        return Result.value(Enum.valueOf(enumClass, argument.toUpperCase(locale)));
      } catch (IllegalArgumentException ignored) {
        // ignored
      }
    }
    return Result.error(new ParameterArgumentException(
        translatable("enum.transform.invalid", text(argument.toLowerCase(locale)),
            errors.getUnchecked(enumClass)), parameter));
  }
}
