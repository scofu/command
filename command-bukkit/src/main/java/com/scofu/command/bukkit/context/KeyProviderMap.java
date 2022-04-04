package com.scofu.command.bukkit.context;

import com.google.inject.Inject;
import com.scofu.command.ConcurrentDynamicMap;
import com.scofu.command.text.HelpMessageGenerator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.command.CommandSender;

/**
 * A concurrent dynamic map of key providers.
 */
public class KeyProviderMap
    extends ConcurrentDynamicMap<CommandSender, KeyProvider<? super CommandSender>> {

  private final Map<UUID, CommandSenderContext> cache;
  private final HelpMessageGenerator helpMessageGenerator;

  @Inject
  KeyProviderMap(HelpMessageGenerator helpMessageGenerator) {
    this.helpMessageGenerator = helpMessageGenerator;
    this.cache = new ConcurrentHashMap<>();
  }

  /**
   * Gets or creates a new context for the given command sender.
   *
   * @param commandSender the command sender
   * @param locale        the locale
   */
  public Optional<CommandSenderContext> getOrCreateContext(CommandSender commandSender,
      Locale locale) {
    return get(commandSender).map(keyProvider -> keyProvider.provide(commandSender))
        .map(uuid -> cache.computeIfAbsent(uuid,
            unused -> new CommandSenderContext(helpMessageGenerator, commandSender, locale)));
  }

  @Override
  public Optional<KeyProvider<? super CommandSender>> invalidate(CommandSender key) {
    return super.invalidate(key).map(keyProvider -> {
      cache.remove(keyProvider.provide(key));
      return keyProvider;
    });
  }

}
