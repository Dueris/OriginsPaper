package io.github.dueris.originspaper.access;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface OwnableAttributeInstance {
	@Nullable Entity apoli$getOwner();

	void apoli$setOwner(Entity owner);
}
