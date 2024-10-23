package io.github.dueris.originspaper.registry;

import io.github.dueris.calio.data.ClassDataRegistry;
import io.github.dueris.originspaper.power.type.PowerType;

public class ApoliClassData {

	public static void registerAll() {
		ClassDataRegistry<PowerType> power = ClassDataRegistry.getOrCreate(PowerType.class, "PowerType");
		power.addPackage("io.github.dueris.originspaper.power.type");
	}
}
