package me.dueris.originspaper.factory.conditions;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.types.*;
import me.dueris.originspaper.util.Reflector;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public enum ConditionTypes {
	BIENTITY,
	BIOME,
	BLOCK,
	DAMAGE,
	ENTITY,
	FLUID,
	ITEM;

	public static class ConditionFactory {
		public static void addMetaConditions() {
			List<Class> classes = new ArrayList<>();
			String[] names = {"and", "or", "chance", "constant", "not"};
			classes.addAll(List.of(BiEntityConditions.class, BiomeConditions.class, BlockConditions.class, DamageConditions.class, EntityConditions.class, FluidConditions.class, ItemConditions.class));
			classes.forEach(c -> {
				try {
					Class factoryInstance = Class.forName(c.getName() + "$ConditionFactory");
					for (String name : names) {
						Object inst = factoryInstance.getConstructor(c, ResourceLocation.class, BiPredicate.class).newInstance(c.newInstance(), OriginsPaper.apoliIdentifier(name), new BiPredicate() {
							@Override
							public boolean test(Object o, Object o2) {
								throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
							}
						});
						Reflector.accessMethod$Invoke("register", c, c.newInstance(), new Class[]{inst.getClass()}, inst);
					}
				} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
						 IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}
}
