package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.locator.HookLocator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
	Class<? extends HookLocator> locator();

	String method();
}
