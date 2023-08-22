package me.dueris.genesismc.core.factory.powers.OriginsMod.world;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashSet;

import static me.dueris.genesismc.core.factory.powers.Powers.entity_glow;

public class EntityGlow extends BukkitRunnable {

    public Collection<Entity> getEntitiesInRadius(Player player, int radius) {
        Collection<Entity> entitiesInRadius = new HashSet<>();
        for (Entity entity : player.getLocation().getWorld().getEntities()) {
            if (entity.getLocation().distance(player.getLocation()) <= radius) {
                entitiesInRadius.add(entity);
            }
        }
        return entitiesInRadius;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                if (entity_glow.contains(p)) {
                    Collection<Entity> entitiesWithinRadius = getEntitiesInRadius(p, 10);
                    for (Entity entity : entitiesWithinRadius) {
                        ConditionExecutor conditionExecutor = new ConditionExecutor();
                        if (conditionExecutor.check("condition", "conditions", p, origin, "origins:entity_glow", null, entity)) {
                            CraftPlayer craftPlayer = (CraftPlayer) p;
                            MobEffect effect = MobEffects.GLOWING;
                            craftPlayer.getHandle().connection.send(new ClientboundUpdateMobEffectPacket(entity.getEntityId(), new MobEffectInstance(effect, 60, 2, false, false, false)));
                        }
                    }
                }
            }
        }
    }
}
