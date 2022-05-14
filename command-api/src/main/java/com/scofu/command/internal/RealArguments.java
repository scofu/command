package com.scofu.command.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.kyori.adventure.text.Component.translatable;

import com.scofu.command.ParameterArgumentException;
import com.scofu.command.PeekableIterator;
import com.scofu.command.Result;
import com.scofu.command.model.Parameter;
import com.scofu.command.target.Arguments;
import com.scofu.command.target.Escapable;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Real arguments.
 */
public class RealArguments implements Arguments {

  private final PeekableIterator<String> iterator;

  private RealArguments(Iterator<String> iterator) {
    this.iterator = PeekableIterator.wrap(iterator);
  }

  /**
   * Creates and returns a new real arguments.
   *
   * @param iterator the iterator
   */
  public static RealArguments newRealArguments(Iterator<String> iterator) {
    checkNotNull(iterator, "iterator");
    return new RealArguments(iterator);
  }

  @Override
  public boolean hasNext() {
    return iterator.peek().filter(s -> !s.isEmpty()).isPresent();
  }

  @Override
  public boolean hasNextOrEmptyNext() {
    return iterator.hasNext();
  }

  @Override
  public String next() {
    return iterator.next();
  }

  @Override
  public <T> Result<String> nextQuotable(Parameter<T> parameter) {
    checkNotNull(parameter, "parameter");
    final var escapable = parameter.isAnnotationPresent(Escapable.class);
    final var builder = new StringBuilder();
    var expectingEnclosingQuote = false;
    while (hasNextOrEmptyNext()) {
      final var string = next();
      var start = 0;
      var end = string.length();
      if (!expectingEnclosingQuote && string.startsWith("\"")) {
        expectingEnclosingQuote = true;
        start = 1;
      }
      if (expectingEnclosingQuote && !string.endsWith("\\\"") && string.endsWith("\"")) {
        if (!builder.isEmpty() || (builder.isEmpty() && string.length() > 1)) {
          expectingEnclosingQuote = false;
        }
        end -= 1;
      }
      if (expectingEnclosingQuote) {
        if (!hasNextOrEmptyNext()) {
          return Result.errorValue(
              new ParameterArgumentException(translatable("argument.missing_enclosing_quote"),
                  parameter), builder.append(string).toString());
        }
      }
      if (end - start >= 1) {
        if (escapable) {
          translateEscapes(string, builder, start, end);
        } else {
          builder.append(string, start, end);
        }
      }
      if (expectingEnclosingQuote) {
        builder.append(" ");
        continue;
      }
      return Result.value(builder.toString());
    }
    return Result.empty();
  }

  @Override
  public Stream<String> remaining() {
    return hasNext() ? Stream.iterate(next(), Objects::nonNull, unused -> hasNext() ? next() : null)
        : Stream.empty();
  }

  private void translateEscapes(String string, StringBuilder builder, int start, int end) {
    for (int i = start; i < end; i++) {
      char character = string.charAt(i);
      if (character == '\\') {
        char next = (i == string.length() - 1) ? '\\' : string.charAt(i + 1);
        // Octal escape?
        if (next >= '0' && next <= '7') {
          var code = "" + next;
          i++;
          if ((i < string.length() - 1) && string.charAt(i + 1) >= '0'
              && string.charAt(i + 1) <= '7') {
            code += string.charAt(i + 1);
            i++;
            if ((i < string.length() - 1) && string.charAt(i + 1) >= '0'
                && string.charAt(i + 1) <= '7') {
              code += string.charAt(i + 1);
              i++;
            }
          }
          builder.append((char) Integer.parseInt(code, 8));
          continue;
        }
        switch (next) {
          case 'b' -> character = '\b';
          case 'f' -> character = '\f';
          case 'n' -> character = '\n';
          case 'r' -> character = '\r';
          case 't' -> character = '\t';
          case '\"' -> character = '\"';
          case '\'' -> character = '\'';
          case 'u' -> {
            if (i >= string.length() - 5) {
              character = 'u';
              break;
            }
            int code = Integer.parseInt(
                "" + string.charAt(i + 2) + string.charAt(i + 3) + string.charAt(i + 4)
                    + string.charAt(i + 5), 16);
            builder.append(Character.toChars(code));
            i += 5;
            continue;
          }
          default -> {
          }
        }
        i++;
      }
      builder.append(character);
    }
  }
}
