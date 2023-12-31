package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_falling;

public class ModifyFallingPower extends CraftPower implements Listener {

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

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void runE(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (modify_falling.contains(p)) {
            if (e.getTo().getY() == e.getFrom().getY()) return;
            @NotNull Vector velocity = p.getVelocity();
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (conditionExecutor.check("condition", "conditions", p, power, "origins:modify_falling", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                        if (power.getObject("velocity") instanceof Integer) {
                            if (Integer.parseInt(power.get("velocity")) < 0) {
                                //greaterthan
                                velocity.setY(Integer.parseInt(power.get("velocity")));
                                p.setVelocity(velocity);
                            } else {
                                //smallerthan
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1, false, false, false));
                            }
                        } else if (power.getObject("velocity") instanceof Float) {
                            if (Float.parseFloat(power.get("velocity")) < 0) {
                                //greaterthan
                                velocity.setY(Float.parseFloat(power.get("velocity")));
                                p.setVelocity(velocity);
                            } else {
                                //smallerthan
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1, false, false, false));
                            }
                        } else if (power.getObject("velocity") instanceof Double) {
                            if (Double.parseDouble(power.get("velocity")) < 0) {
                                //greaterthan
                                velocity.setY(Double.parseDouble(power.get("velocity")));
                                p.setVelocity(velocity);
                            } else {
                                //smallerthan
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1, false, false, false));
                            }
                        } else if (power.getObject("velocity") instanceof Long) {
                            if (Long.parseLong(power.get("velocity")) < 0) {
                                //greaterthan
                                velocity.setY(Long.parseLong(power.get("velocity")));
                                p.setVelocity(velocity);
                            } else {
                                //smallerthan
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1, false, false, false));
                            }
                        } else if (power.getObject("velocity") instanceof Short) {
                            if (Short.parseShort(power.get("velocity")) < 0) {
                                //greaterthan
                                velocity.setY(Short.parseShort(power.get("velocity")));
                                p.setVelocity(velocity);
                            } else {
                                //smallerthan
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1, false, false, false));
                            }
                        }
                    }
                }

            }
        }
    }

    @EventHandler
    public void runR(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (modify_falling.contains(p)) {
                for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (conditionExecutor.check("condition", "conditions", p, power, "origins:modify_falling", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            if (Boolean.getBoolean(power.get("take_fall_damage", "true"))) {
                                if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                                    e.setDamage(0);
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:modify_falling";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_falling;
    }
}
