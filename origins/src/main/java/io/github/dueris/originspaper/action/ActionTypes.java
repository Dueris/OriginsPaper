package io.github.dueris.originspaper.action;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.dueris.calio.util.ReflectionUtils;
import io.github.dueris.originspaper.action.types.BiEntityActions;
import io.github.dueris.originspaper.action.types.BlockActions;
import io.github.dueris.originspaper.action.types.EntityActions;
import io.github.dueris.originspaper.action.types.ItemActions;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class ActionTypes {

	public static void registerAll() {
		BiEntityActions.registerAll();
		BlockActions.registerAll();
		EntityActions.registerAll();
		ItemActions.registerAll();
	}

	public static <T> void registerPackage(@NotNull Consumer<ActionTypeFactory<T>> factoryConsumer, String directory) {
		try {
			ScanResult result = new ClassGraph().whitelistPackages(directory).enableClassInfo().scan();

			try {
				result.getAllClasses().loadClasses()
					.stream()
					.filter(clz -> !clz.isAnnotation() && !clz.isInterface() && !clz.isEnum())
					.forEach(
						clz -> {
							try {
								ActionTypeFactory<T> factory = ReflectionUtils.invokeStaticMethod(clz, "getFactory");
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
