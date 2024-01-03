package me.dueris.genesismc.factory.powers.world;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class EntityGlow extends CraftPower {

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
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
    public void run(Player p) {
        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            if (entity_glow.contains(p)) {
                Collection<Entity> entitiesWithinRadius = getEntitiesInRadius(p, 10);
                for (Entity entity : entitiesWithinRadius) {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (conditionExecutor.check("condition", "conditions", p, power, getPowerFile(), p, entity, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            if(conditionExecutor.check("bientity_condition", "bientity_conditions", p, power, getPowerFile(), p, entity, p.getLocation().getBlock(), null, p.getActiveItem(), null)){
                                CraftPlayer craftPlayer = (CraftPlayer) p;
//                                MobEffect effect = MobEffects.GLOWING;
//                                craftPlayer.getHandle().connection.send(new ClientboundUpdateMobEffectPacket(entity.getEntityId(), new MobEffectInstance(effect, 60, 2, false, false, false)));
                                if(!entity.isGlowing()){
                                    entity.setGlowing(true);
                                }
                                setActive(p, power.getTag(), true);
                            } else {
                                if(entity.isGlowing()){
                                    entity.setGlowing(false);
                                }
                                setActive(p, power.getTag(), false);
                            }
                        } else {
                            if(entity.isGlowing()){
                                entity.setGlowing(false);
                            }
                            setActive(p, power.getTag(), false);
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
