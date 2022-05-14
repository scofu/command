package com.scofu.command;

import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import com.scofu.command.internal.InternalCommandModule;
import com.scofu.command.model.DiscoveryConfiguration;
import com.scofu.command.model.DiscoveryListener;
import com.scofu.command.model.DiscoveryManager;
import com.scofu.command.model.TransformingNodeDiscoverer;
import com.scofu.command.target.TransformerManager;
import com.scofu.command.target.TransformerMap;
import com.scofu.command.target.TransformingSuggester;
import com.scofu.command.target.TransformingTarget;
import com.scofu.command.text.DescriberManager;
import com.scofu.command.text.DescriberMap;
import com.scofu.command.text.DescriptionDiscoveryListener;
import com.scofu.command.text.DescriptionUsageGenerator;
import com.scofu.command.text.HelpMessageConfiguration;
import com.scofu.command.text.HelpMessageGenerator;
import com.scofu.command.text.UsageGenerator;
import com.scofu.command.validation.PermissionDiscoveryListener;
import com.scofu.command.validation.PermissionValidator;
import com.scofu.command.validation.Validator;
import com.scofu.common.inject.AbstractFeatureModule;
import com.scofu.common.inject.annotation.Module;
import com.scofu.text.BundledTranslationProvider;
import java.util.Locale;

/** Binds command interfaces. */
@Module
public class CommandModule extends AbstractFeatureModule {

  @Override
  protected void configure() {
    install(new InternalCommandModule());
    bind(TransformerMap.class).in(Scopes.SINGLETON);
    bind(TransformingTarget.class).in(Scopes.SINGLETON);
    bind(TransformingSuggester.class).in(Scopes.SINGLETON);
    bind(TransformingNodeDiscoverer.class).in(Scopes.SINGLETON);
    bind(DescriberMap.class).in(Scopes.SINGLETON);
    bind(HelpMessageGenerator.class).in(Scopes.SINGLETON);
    Multibinder.newSetBinder(binder(), Validator.class)
        .addBinding()
        .to(PermissionValidator.class)
        .in(Scopes.SINGLETON);
    bindFeatureManager(DiscoveryManager.class).in(Scopes.SINGLETON);
    bindFeatureManager(TransformerManager.class).in(Scopes.SINGLETON);
    bindFeatureManager(DescriberManager.class).in(Scopes.SINGLETON);
    bindFeatureInstance(
        new BundledTranslationProvider(
            Locale.US, "command_en_US", CommandModule.class.getClassLoader()));

    final var discoveryListenerBinder = Multibinder.newSetBinder(binder(), DiscoveryListener.class);
    discoveryListenerBinder.addBinding().to(PermissionDiscoveryListener.class).in(Scopes.SINGLETON);
    discoveryListenerBinder
        .addBinding()
        .to(DescriptionDiscoveryListener.class)
        .in(Scopes.SINGLETON);

    OptionalBinder.newOptionalBinder(binder(), UsageGenerator.class)
        .setDefault()
        .to(DescriptionUsageGenerator.class)
        .in(Scopes.SINGLETON);

    OptionalBinder.newOptionalBinder(binder(), DiscoveryConfiguration.class)
        .setDefault()
        .toProvider(DiscoveryConfiguration::newDefaultConfiguration)
        .in(Scopes.SINGLETON);

    OptionalBinder.newOptionalBinder(binder(), HelpMessageConfiguration.class)
        .setDefault()
        .toProvider(HelpMessageConfiguration::newDefaultConfiguration)
        .in(Scopes.SINGLETON);
  }
}
