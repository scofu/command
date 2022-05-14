package com.scofu.command.text;

import com.scofu.command.ConcurrentDynamicMap;
import java.lang.reflect.Type;

/** A concurrent dynamic map of describers. */
public class DescriberMap extends ConcurrentDynamicMap<Type, Describer<?>> {}
