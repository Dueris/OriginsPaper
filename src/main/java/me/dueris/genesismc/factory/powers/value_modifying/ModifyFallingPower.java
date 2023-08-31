package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
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

import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_falling;

public class ModifyFallingPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }


    @EventHandler
    public void runE(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (modify_falling.contains(p)) {
            if(e.getTo().getY() == e.getFrom().getY()) return;
            @NotNull Vector velocity = p.getVelocity();
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if (conditionExecutor.check("condition", "conditions", p, origin, "origins:modify_falling", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                    if (origin.getPowerFileFromType(getPowerFile()).getObject("velocity") instanceof Integer) {
                        if (Integer.parseInt(origin.getPowerFileFromType(getPowerFile()).get("velocity").toString()) < 0) {
                            //greaterthan
                            velocity.setY(Integer.parseInt(origin.getPowerFileFromType(getPowerFile()).get("velocity").toString()));
                            p.setVelocity(velocity);
                        } else {
                            //smallerthan
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1, false, false, false));
                        }
                    } else if (origin.getPowerFileFromType(getPowerFile()).getObject("velocity") instanceof Float) {
                        if (Float.parseFloat(origin.getPowerFileFromType(getPowerFile()).get("velocity").toString()) < 0) {
                            //greaterthan
                            velocity.setY(Float.parseFloat(origin.getPowerFileFromType(getPowerFile()).get("velocity").toString()));
                            p.setVelocity(velocity);
                        } else {
                            //smallerthan
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1, false, false, false));
                        }
                    } else if (origin.getPowerFileFromType(getPowerFile()).getObject("velocity") instanceof Double) {
                        if (Double.parseDouble(origin.getPowerFileFromType(getPowerFile()).get("velocity").toString()) < 0) {
                            //greaterthan
                            velocity.setY(Double.parseDouble(origin.getPowerFileFromType(getPowerFile()).get("velocity").toString()));
                            p.setVelocity(velocity);
                        } else {
                            //smallerthan
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1, false, false, false));
                        }
                    } else if (origin.getPowerFileFromType(getPowerFile()).getObject("velocity") instanceof Long) {
                        if (Long.parseLong(origin.getPowerFileFromType(getPowerFile()).get("velocity").toString()) < 0) {
                            //greaterthan
                            velocity.setY(Long.parseLong(origin.getPowerFileFromType(getPowerFile()).get("velocity").toString()));
                            p.setVelocity(velocity);
                        } else {
                            //smallerthan
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1, false, false, false));
                        }
                    } else if (origin.getPowerFileFromType(getPowerFile()).getObject("velocity") instanceof Short) {
                        if (Short.parseShort(origin.getPowerFileFromType(getPowerFile()).get("velocity").toString()) < 0) {
                            //greaterthan
                            velocity.setY(Short.parseShort(origin.getPowerFileFromType(getPowerFile()).get("velocity").toString()));
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

    @EventHandler
    public void runR(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (modify_falling.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if (conditionExecutor.check("condition", "conditions", p, origin, "origins:modify_falling", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                        if (Boolean.getBoolean(origin.getPowerFileFromType("origins:modify_falling").get("take_fall_damage", "true"))) {
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

    @Override
    public void run() {

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
