package com.scofu.command.bukkit;

import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import com.scofu.command.model.DiscoveryListener;
import com.scofu.command.text.HelpMessageConfiguration;
import com.scofu.common.inject.AbstractFeatureModule;
import com.scofu.common.inject.annotation.Module;
import com.scofu.text.BundledTranslationProvider;
import java.util.Locale;

/** Command bukkit module. */
@Module
public class CommandBukkitModule extends AbstractFeatureModule {

  @Override
  protected void configure() {
    bindFeature(PlayerTransformer.class).in(Scopes.SINGLETON);
    bindFeature(PlayerDescriber.class).in(Scopes.SINGLETON);
    bindFeatureInstance(
        new BundledTranslationProvider(
            Locale.US, "command-bukkit_en_US", CommandBukkitModule.class.getClassLoader()));

    Multibinder.newSetBinder(binder(), DiscoveryListener.class)
        .addBinding()
        .to(ForwardingDiscoveryListener.class)
        .in(Scopes.SINGLETON);

    OptionalBinder.newOptionalBinder(binder(), HelpMessageConfiguration.class)
        .setBinding()
        .toProvider(() -> HelpMessageConfiguration.builder().withCommandPrefix("/").build())
        .in(Scopes.SINGLETON);
  }
}
