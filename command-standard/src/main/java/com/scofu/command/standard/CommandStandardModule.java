package com.scofu.command.standard;

import com.google.inject.Scopes;
import com.scofu.common.inject.AbstractFeatureModule;
import com.scofu.common.inject.annotation.Module;
import com.scofu.text.BundledTranslationProvider;
import java.util.Locale;

/** Binds standard describers and transformers. */
@Module
public class CommandStandardModule extends AbstractFeatureModule {

  @Override
  protected void configure() {
    bindFeature(BooleanDescriber.class).in(Scopes.SINGLETON);
    bindFeature(DurationDescriber.class).in(Scopes.SINGLETON);
    bindFeature(EnumDescriber.class).in(Scopes.SINGLETON);
    bindFeature(IntegerDescriber.class).in(Scopes.SINGLETON);
    bindFeature(OptionalDescriber.class).in(Scopes.SINGLETON);
    bindFeature(StringDescriber.class).in(Scopes.SINGLETON);

    bindFeature(BooleanTransformer.class).in(Scopes.SINGLETON);
    bindFeature(ContextTransformer.class).in(Scopes.SINGLETON);
    bindFeature(DurationTransformer.class).in(Scopes.SINGLETON);
    bindFeature(EnumTransformer.class).in(Scopes.SINGLETON);
    bindFeature(ExpansionTransformer.class).in(Scopes.SINGLETON);
    bindFeature(IntegerTransformer.class).in(Scopes.SINGLETON);
    bindFeature(OptionalTransformer.class).in(Scopes.SINGLETON);
    bindFeature(StringTransformer.class).in(Scopes.SINGLETON);

    bindFeatureInstance(
        new BundledTranslationProvider(
            Locale.US, "command-standard_en_US", CommandStandardModule.class.getClassLoader()));
  }
}
