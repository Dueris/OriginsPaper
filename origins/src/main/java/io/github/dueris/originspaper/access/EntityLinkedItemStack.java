package io.github.dueris.originspaper.access;

import net.minecraft.world.entity.Entity;

public interface EntityLinkedItemStack {
	Entity apoli$getEntity();

	Entity apoli$getEntity(boolean prioritiseVanillaHolder);

	void apoli$setEntity(Entity entity);
}
