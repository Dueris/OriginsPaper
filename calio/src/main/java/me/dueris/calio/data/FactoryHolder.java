package me.dueris.calio.data;

import me.dueris.calio.registry.Registrable;
import net.minecraft.resources.ResourceLocation;

public interface FactoryHolder extends Registrable {
	static FactoryData registerComponents(FactoryData data) {
		return data;
	}

	FactoryHolder ofResourceLocation(ResourceLocation var1);

	default void bootstrap() {
	}

	default boolean canRegister() {
		return true;
	}
}
