package me.dueris.originspaper.factory.conditions;

import me.dueris.originspaper.factory.conditions.types.*;

public class Conditions {

	public static void registerAll() {
		BiEntityConditions.registerConditions();
		BiomeConditions.registerConditions();
		BlockConditions.registerConditions();
		DamageConditions.registerConditions();
		EntityConditions.registerConditions();
		FluidConditions.registerConditions();
		ItemConditions.registerConditions();
	}
}
