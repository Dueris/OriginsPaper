package io.github.dueris.originspaper.registry;

import io.github.dueris.calio.data.ClassDataRegistry;
import io.github.dueris.originspaper.power.type.PowerType;

public class ApoliClassData {

	public static final ClassDataRegistry<PowerType> POWER_TYPE = ClassDataRegistry.getOrCreate(PowerType.class, "PowerType");

	public static void registerAll() {
		POWER_TYPE.addPackage("io.github.apace100.apoli.power.type");
	}

}
