package com.scofu.command.internal;

import com.google.inject.Scopes;
import com.scofu.command.Dispatcher;
import com.scofu.common.inject.AbstractFeatureModule;

/** Binds internal implementations. */
public class InternalCommandModule extends AbstractFeatureModule {

  @Override
  protected void configure() {
    bind(Dispatcher.class).to(RealDispatcher.class).in(Scopes.SINGLETON);
  }
}
