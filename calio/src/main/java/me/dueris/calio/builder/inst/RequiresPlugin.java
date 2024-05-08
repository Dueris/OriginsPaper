package me.dueris.calio.builder.inst;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPlugin {
	String pluginName() default "genesismc";
}
