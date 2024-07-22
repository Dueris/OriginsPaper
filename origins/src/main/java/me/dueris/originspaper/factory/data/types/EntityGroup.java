package me.dueris.originspaper.factory.data.types;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public enum EntityGroup {
	UNDEFINED(), UNDEAD(), ARTHROPOD(), ILLAGER(), AQUATIC();

	public static EntityGroup getMobType(@NotNull Entity entity) {
		if (entity.getType().is(EntityTypeTags.ARTHROPOD)) return ARTHROPOD;
		if (entity.getType().is(EntityTypeTags.UNDEAD)) return UNDEAD;
		if (entity.getType().is(EntityTypeTags.ARTHROPOD)) return ARTHROPOD;
		if (entity.getType().is(EntityTypeTags.ILLAGER)) return ILLAGER;
		if (entity.getType().is(EntityTypeTags.AQUATIC)) return AQUATIC;
		return UNDEFINED; // Entity not in either of those
	}
}
