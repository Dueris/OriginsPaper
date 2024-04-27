package me.dueris.genesismc.factory.data.types;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;

public enum EntityGroup {
    UNDEFINED(), UNDEAD(), ARTHROPOD(), ILLAGER(), WATER();

    public static EntityGroup getMobType(Entity entity) {
        if (entity.getType().is(EntityTypeTags.ARTHROPOD)) return ARTHROPOD;
        if (entity.getType().is(EntityTypeTags.UNDEAD)) return UNDEAD;
        if (entity.getType().is(EntityTypeTags.ARTHROPOD)) return ARTHROPOD;
        if (entity.getType().is(EntityTypeTags.ILLAGER)) return ILLAGER;
        if (entity.getType().is(EntityTypeTags.AQUATIC)) return WATER;
        return UNDEFINED; // Entity not in either of those
    }
}
