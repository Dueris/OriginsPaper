package me.dueris.originspaper.factory.conditions;

import me.dueris.originspaper.factory.conditions.types.*;

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
}
