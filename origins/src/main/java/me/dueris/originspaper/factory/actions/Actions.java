package me.dueris.originspaper.factory.actions;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.dueris.calio.util.ReflectionUtils;
import me.dueris.originspaper.factory.actions.types.BiEntityActions;
import me.dueris.originspaper.factory.actions.types.BlockActions;
import me.dueris.originspaper.factory.actions.types.EntityActions;
import me.dueris.originspaper.factory.actions.types.ItemActions;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class Actions {

	public static void registerAll() {
		BiEntityActions.registerAll();
		BlockActions.registerAll();
		EntityActions.registerAll();
		ItemActions.registerAll();
	}

	public static <T> void registerPackage(@NotNull Consumer<ActionFactory<T>> factoryConsumer, String subPackage) {
		try {
			ScanResult result = new ClassGraph().whitelistPackages("me.dueris.originspaper.factory.actions.types." + subPackage).enableClassInfo().scan();

			try {
				result.getAllClasses().loadClasses()
					.stream()
					.filter(clz -> !clz.isAnnotation() && !clz.isInterface() && !clz.isEnum())
					.forEach(
						clz -> {
							try {
								ActionFactory<T> factory = ReflectionUtils.invokeStaticMethod(clz, "getFactory");
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
