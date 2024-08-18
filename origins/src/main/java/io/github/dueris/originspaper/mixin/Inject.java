package io.github.dueris.originspaper.mixin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
	At.Value locator();

	String method();

	Class<?>[] params() default {};
}
