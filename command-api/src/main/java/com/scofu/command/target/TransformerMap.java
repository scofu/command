package com.scofu.command.target;

import com.scofu.command.ConcurrentDynamicMap;
import java.lang.reflect.Type;

/**
 * A concurrent dynamic map of transformers.
 */
public class TransformerMap extends ConcurrentDynamicMap<Type, Transformer<?>> {}
