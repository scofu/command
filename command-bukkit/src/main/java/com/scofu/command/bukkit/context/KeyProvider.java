package com.scofu.command.bukkit.context;

import com.scofu.common.inject.Feature;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.command.CommandSender;

/**
 * Provides keys (uuids) for command senders.
 *
 * @param <T> the type of the command sender
 */
public interface KeyProvider<T extends CommandSender> extends Feature, Predicate<CommandSender> {

  UUID provide(T t);

}
