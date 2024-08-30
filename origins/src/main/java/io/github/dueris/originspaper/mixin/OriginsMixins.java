package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.dragoncommissions.mixbukkit.MixinPluginInstance;
import com.dragoncommissions.mixbukkit.addons.AutoMapper;
import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.api.action.impl.MActionInsertShellCode;
import com.dragoncommissions.mixbukkit.api.action.impl.MActionMethodReplacer;
import com.dragoncommissions.mixbukkit.api.locator.HookLocator;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.ShellCodeReflectionMixinPluginMethodCall;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.dueris.originspaper.OriginsPaper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class OriginsMixins {

	public static void init(@NotNull MixBukkit bukkit) {
		MixinPlugin mixinPlugin = bukkit.registerMixinPlugin(new MixinPluginInstance("OriginsPaper"), AutoMapper.getMappingAsStream());
		ScanResult result = new ClassGraph().whitelistPackages("io.github.dueris.originspaper.mixin").enableClassInfo().scan();

		OriginsPaper.LOGGER.info("Starting Mixin transformers...");
		AtomicInteger count = new AtomicInteger();

		try {
			result.getAllClasses().loadClasses()
				.stream()
				.filter(clz -> !clz.isAnnotation() && !clz.isInterface() && !clz.isEnum())
				.forEach(
					clz -> {
						if (clz.isAnnotationPresent(Mixin.class)) {
							Mixin mixinData = clz.getAnnotation(Mixin.class);
							Class<?> mixin = mixinData.value()[0];
							String baseNamespace = mixin.getSimpleName() + "=Mixin";
							for (Method method : clz.getDeclaredMethods()) {
								if (method.isAnnotationPresent(Inject.class)) {
									Inject inject = method.getAnnotation(Inject.class);
									HookLocator locatorInstance = inject.locator().getLocator();

									MActionInsertShellCode shellCode = new MActionInsertShellCode(
										new ShellCodeReflectionMixinPluginMethodCall(method), locatorInstance
									);

									Method toMixin = null;
									Class<?>[] params = new Class[0];
									for (Method declared : mixin.getDeclaredMethods()) {
										String methodName = declared.getName();
										String injectMethodName = inject.method().trim();

										if (methodName.equalsIgnoreCase(injectMethodName)) {
											toMixin = declared;
											params = declared.getParameterTypes();
											break;
										}
									}
									if (toMixin == null)
										throw new IllegalArgumentException("Unable to locate method to mixin to!");

									if (!Arrays.stream(inject.params()).toList().isEmpty()) {
										params = inject.params();
									}

									String namespace = baseNamespace + "(" + method.getName() + ")";
									mixinPlugin.registerMixin(
										namespace, shellCode, mixin, toMixin.getName(), toMixin.getReturnType(), params
									);

									count.getAndIncrement();
								} else if (method.isAnnotationPresent(Overwrite.class)) {
									Overwrite replace = method.getAnnotation(Overwrite.class);

									Method toMixin = null;
									Class<?>[] params = new Class[0];
									for (Method declared : mixin.getDeclaredMethods()) {
										String methodName = declared.getName();
										String injectMethodName = replace.method().trim();

										if (methodName.equalsIgnoreCase(injectMethodName)) {
											toMixin = declared;
											params = declared.getParameterTypes();
											break;
										}
									}
									if (toMixin == null)
										throw new IllegalArgumentException("Unable to locate method to mixin to!");

									if (!Arrays.stream(replace.params()).toList().isEmpty()) {
										params = replace.params();
									}

									MActionMethodReplacer replacer = new MActionMethodReplacer(toMixin);
									String namespace = baseNamespace + "(" + method.getName() + ")";
									mixinPlugin.registerMixin(
										namespace, replacer, mixin, toMixin.getName(), toMixin.getReturnType(), params
									);
								}
							}
						}
					}
				);
		} catch (Throwable var5) {
			if (result != null) {
				try {
					result.close();
				} catch (Throwable var4) {
					var5.addSuppressed(var4);
				}
			}

			throw var5;
		}

		result.close();
		OriginsPaper.LOGGER.info("Injected {} mixins successfully!", count.toString());
	}
}
