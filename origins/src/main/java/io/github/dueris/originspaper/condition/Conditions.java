package io.github.dueris.originspaper.condition;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.dueris.calio.util.ReflectionUtils;
import io.github.dueris.originspaper.condition.types.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class Conditions {

	public static void registerAll() {
		BiEntityConditions.registerAll();
		BiomeConditions.registerAll();
		BlockConditions.registerAll();
		DamageConditions.registerAll();
		EntityConditions.registerAll();
		FluidConditions.registerAll();
		ItemConditions.registerAll();
	}

	public static <T> void registerPackage(@NotNull Consumer<ConditionFactory<T>> factoryConsumer, String directory) {
		try {
			ScanResult result = new ClassGraph().whitelistPackages(directory).enableClassInfo().scan();

			try {
				result.getAllClasses().loadClasses()
					.stream()
					.filter(clz -> !clz.isAnnotation() && !clz.isInterface() && !clz.isEnum())
					.forEach(
						clz -> {
							try {
								ConditionFactory<T> factory = ReflectionUtils.invokeStaticMethod(clz, "getFactory");
								factoryConsumer.accept(factory);
							} catch (InvocationTargetException | IllegalAccessException |
									 NoSuchMethodException e) {
								throw new RuntimeException(e);
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
		} catch (Exception var6) {
			System.out.println("This would've been a zip error :P. Please tell us on discord if you see this ^-^");
			var6.printStackTrace();
		}
	}
}
