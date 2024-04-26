package me.dueris.genesismc.factory.data.types;

import net.minecraft.world.entity.MobType;

public enum EntityGroup {
    UNDEFINED(MobType.UNDEFINED), UNDEAD(MobType.UNDEAD), ARTHROPOD(MobType.ARTHROPOD), ILLAGER(MobType.ILLAGER), WATER(MobType.WATER);
    final MobType nms;

    EntityGroup(MobType nms) {
        this.nms = nms;
    }

    public MobType nms() {
        return this.nms;
    }
}
