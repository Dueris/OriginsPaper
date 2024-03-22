package me.dueris.genesismc.factory.data;

import me.dueris.genesismc.factory.data.types.ExplosionMask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;

public class DataTypes {
    public static ExplosionMask getExplosionMask(Explosion explosion, ServerLevel level){
        return new ExplosionMask(explosion, level);
    }
}
