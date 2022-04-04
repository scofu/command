package com.scofu.command.standard;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.TranslationRegistry;

final class BooleanTransformer implements Transformer<Boolean> {

  private final LoadingCache<Locale, List<LiteralBooleanTranslation>> translationCache;

  @Inject
  BooleanTransformer(TranslationRegistry translationRegistry) {
    translationCache = CacheBuilder.newBuilder()
        .expireAfterAccess(1, TimeUnit.HOURS)
        .build(new CacheLoader<>() {
          @Override
          public List<LiteralBooleanTranslation> load(Locale key) throws Exception {
            final List<LiteralBooleanTranslation> list = new LinkedList<>();
            list.add(literal(translationRegistry, key, "true", true));
            list.add(literal(translationRegistry, key, "false", false));
            list.add(literal(translationRegistry, key, "yes", true));
            list.add(literal(translationRegistry, key, "no", false));
            return list;
          }
        });
  }

  @Override
  public boolean test(Type type) {
    return type instanceof Class rawType
        && (Boolean.class.isAssignableFrom(rawType) || boolean.class.isAssignableFrom(rawType));
  }

  @Override
  public Result<Boolean> transform(Command command, Parameter<Boolean> parameter,
      Parameters parameters, Arguments arguments) {
    if (!arguments.hasNext()) {
      return Result.empty();
    }
    return arguments.nextQuotable(parameter)
        .flatMap(string -> parseBoolean(string, parameter, command.context().locale()));
  }

  @Override
  public Stream<String> suggest(Command command, Parameter<Boolean> parameter,
      Parameters parameters, Result<String> argument) {
    if (argument.hasError()) {
      return Stream.of("\"");
    }
    return translationCache.getUnchecked(command.context().locale())
        .stream()
        .map(LiteralBooleanTranslation::translation);
  }

  private LiteralBooleanTranslation literal(TranslationRegistry registry, Locale locale, String key,
      boolean value) {
    return new LiteralBooleanTranslation(
        registry.translate("boolean.literal." + key, locale).format(null), value);
  }

  private Result<Boolean> parseBoolean(String string, Parameter<Boolean> parameter, Locale locale) {
    final var literals = translationCache.getUnchecked(locale);
    for (var literal : literals) {
      if (literal.translation.equalsIgnoreCase(string)) {
        return Result.value(literal.value);
      }
    }
    return Result.error(new ParameterArgumentException(translatable("boolean.transform.mismatch",
        Stream.concat(Stream.of(text(string)), literals.stream()
            .sequential()
            .map(LiteralBooleanTranslation::translation)
            .map(Component::text)).sequential().toList()), parameter));
  }

  private record LiteralBooleanTranslation(String translation, boolean value) {}
}
