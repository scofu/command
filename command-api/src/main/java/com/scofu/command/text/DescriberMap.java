package com.scofu.command.text;

import com.scofu.common.collect.ConcurrentDynamicMap;
import java.lang.reflect.Type;

/** A concurrent dynamic map of describers. */
public class DescriberMap extends ConcurrentDynamicMap<Type, Describer<?>> {}
