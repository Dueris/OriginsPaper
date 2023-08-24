package me.dueris.genesismc.factory.powers.OriginsMod.world;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class EntityGlow extends CraftPower {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

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
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                        }else{
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:entity_glow";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return entity_glow;
    }
}
