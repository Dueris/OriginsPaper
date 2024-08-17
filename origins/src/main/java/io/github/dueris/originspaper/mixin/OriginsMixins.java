package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.dragoncommissions.mixbukkit.addons.AutoMapper;
import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.api.action.impl.MActionInsertShellCode;
import com.dragoncommissions.mixbukkit.api.locator.HookLocator;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.ShellCodeReflectionMixinPluginMethodCall;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.dueris.originspaper.OriginsPaper;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class OriginsMixins {

	public static void init(MixBukkit bukkit) {
		MixinPlugin mixinPlugin = bukkit.registerMixinPlugin(OriginsPaper.getPlugin(), AutoMapper.getMappingAsStream());
		ScanResult result = new ClassGraph().whitelistPackages("io.github.dueris.originspaper.mixin").enableClassInfo().scan();

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
									try {
										HookLocator locatorInstance = inject.locator().newInstance();

										MActionInsertShellCode shellCode = new MActionInsertShellCode(
											new ShellCodeReflectionMixinPluginMethodCall(method), locatorInstance
										);

										Method toMixin = null;
										Class<?>[] params = new Class[0];
										for (Method declared : mixin.getDeclaredMethods()) {
											String methodName = declared.getName();
											String injectMethodName = inject.method().toString().trim();

											if (methodName.equalsIgnoreCase(injectMethodName)) {
												toMixin = declared;
												params = declared.getParameterTypes();
												break;
											}
										}
										if (toMixin == null) throw new IllegalArgumentException("Unable to locate method to mixin to!");

										mixinPlugin.registerMixin(
											baseNamespace + "(" + method.getName() + ")",
											shellCode, mixin, toMixin.getName(), toMixin.getReturnType(), params
										);
									} catch (InstantiationException | IllegalAccessException e) {
										throw new RuntimeException(e);
									}
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
	}
}
