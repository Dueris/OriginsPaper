package io.github.dueris.originspaper.util.fabric;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Collection;
import java.util.Collections;

public interface IdentifiableResourceReloadListener extends PreparableReloadListener {
	/**
	 * @return The unique identifier of this listener.
	 */
	ResourceLocation getFabricId();

	/**
	 * @return The identifiers of listeners this listener expects to have been
	 * executed before itself. Please keep in mind that this only takes effect
	 * during the application stage!
	 */
	default Collection<ResourceLocation> getFabricDependencies() {
		return Collections.emptyList();
	}
}
