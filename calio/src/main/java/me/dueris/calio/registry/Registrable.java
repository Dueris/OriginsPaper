package me.dueris.calio.registry;


import net.minecraft.resources.ResourceLocation;

public interface Registrable {

	/**
	 * Retrieves the namespaced key(or identifier) associated with this object.
	 *
	 * @return the namespaced key
	 */
	ResourceLocation key();
}
