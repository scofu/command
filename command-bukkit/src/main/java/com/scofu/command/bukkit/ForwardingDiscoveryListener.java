package com.scofu.command.bukkit;

import com.google.inject.Inject;
import com.scofu.command.Dispatcher;
import com.scofu.command.model.DiscoveryListener;
import com.scofu.command.model.Node;
import com.scofu.command.text.HelpMessageGenerator;
import com.scofu.command.validation.PermissionDiscoveryListener;
import com.scofu.text.ThemeRegistry;
import java.util.List;
import org.bukkit.Server;

final class ForwardingDiscoveryListener implements DiscoveryListener {

  public static final String FALLBACK_PREFIX = "scofu";

  private final Server server;
  private final Dispatcher dispatcher;
  private final HelpMessageGenerator helpMessageGenerator;
  private final ThemeRegistry themeRegistry;

  @Inject
  ForwardingDiscoveryListener(Server server, Dispatcher dispatcher,
      HelpMessageGenerator helpMessageGenerator, ThemeRegistry themeRegistry) {
    this.server = server;
    this.dispatcher = dispatcher;
    this.helpMessageGenerator = helpMessageGenerator;
    this.themeRegistry = themeRegistry;
  }

  @Override
  public List<Class<? extends DiscoveryListener>> dependencies() {
    return List.of(PermissionDiscoveryListener.class);
  }

  @Override
  public <T, R> void onDiscovery(Node<T, R> node, boolean root) {
    if (!root) {
      return;
    }
    System.out.println("REGISTERING FORWARDING COMMAND: " + node.identifiers());
    server.getCommandMap()
        .register(FALLBACK_PREFIX,
            new ForwardingCommand(node, dispatcher, helpMessageGenerator, themeRegistry));
  }
}
