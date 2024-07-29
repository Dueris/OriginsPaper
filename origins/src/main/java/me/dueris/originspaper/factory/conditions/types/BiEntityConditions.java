package me.dueris.originspaper.factory.conditions.types;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.dueris.calio.util.ReflectionUtils;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.conditions.meta.MetaConditions;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.world.entity.Entity;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class BiEntityConditions implements Listener {
	public static void registerAll() {
		MetaConditions.register(Registries.BIENTITY_CONDITION, BiEntityConditions::register);
		try {
			ScanResult result = new ClassGraph().whitelistPackages("me.dueris.originspaper.factory.conditions.types.bientity").enableClassInfo().scan();

			try {
				result.getAllClasses().loadClasses()
					.stream()
					.filter(clz -> !clz.isAnnotation() && !clz.isInterface() && !clz.isEnum())
					.forEach(
						clz -> {
							try {
								ConditionFactory<Pair<Entity, Entity>> factory = ReflectionUtils.invokeStaticMethod(clz, "getFactory");
								register(factory);
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

	public static void register(@NotNull ConditionFactory<Pair<Entity, Entity>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION).register(factory, factory.getSerializerId());
	}

}
