package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Shape;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class EntityGlowPower extends CraftPower {
    // TODO : https://github.com/SkytAsul/GlowingEntities ?

    @Override
    public void run(Player p, Power power) {
        for (CraftEntity entity : Shape.getEntities(Shape.SPHERE, ((CraftWorld) p.getWorld()).getHandle(), CraftLocation.toVec3D(p.getLocation()), (p.getViewDistance() / 2) * 16).stream().map(Entity::getBukkitEntity).toList()) {
            if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p) &&
                ConditionExecutor.testBiEntity(power.getJsonObject("bientity_condition"), (CraftEntity) p, entity) &&
                ConditionExecutor.testEntity(power.getJsonObject("entity_condition"), entity)
            ) {
                if (entity instanceof LivingEntity le) {
                    p.sendPotionEffectChangeRemove(le, PotionEffectType.GLOWING);
                }

            } else {
                if (entity instanceof LivingEntity le) {
                    p.sendPotionEffectChangeRemove(le, PotionEffectType.GLOWING);
                }
                setActive(p, power.getTag(), false);
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:entity_glow";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return entity_glow;
    }
}
