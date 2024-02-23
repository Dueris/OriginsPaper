package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.AttributeHandler.getOperationMappingsDouble;
import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_projectile_damage;

public class ModifyProjectileDamagePower extends CraftPower implements Listener {

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void runD(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Projectile p && p.getShooter() instanceof Player pl) {
            if (modify_projectile_damage.contains(pl)) {
                for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                    try {
                        ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(pl, getPowerFile(), layer)) {
                            if (conditionExecutor.check("target_condition", "target_conditions", pl, power, getPowerFile(), e.getDamager(), e.getEntity(), p.getLocation().getBlock(), null, pl.getItemInHand(), null) && conditionExecutor.check("damage_condition", "damage_conditions", pl, power, getPowerFile(), e.getDamager(), e.getEntity(), p.getLocation().getBlock(), null, pl.getItemInHand(), e)) {
                                for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
                                    Float value = Float.valueOf(modifier.get("value").toString());
                                    String operation = modifier.get("operation").toString();
                                    BinaryOperator mathOperator = getOperationMappingsDouble().get(operation);
                                    if (mathOperator != null) {
                                        ModifyDamageDealtPower damageDealtPower = new ModifyDamageDealtPower();
                                        damageDealtPower.runSetDMG(e, operation, value);
                                        setActive(pl, power.getTag(), true);
                                        Actions.EntityActionType(e.getEntity(), power.getAction("target_action"));
                                        Actions.EntityActionType(pl, power.getAction("self_action"));
                                    }
                                }
                            } else {

                                setActive(pl, power.getTag(), false);
                            }
                        }
                    } catch (Exception ev) {
                        ev.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_projectile_damage";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_projectile_damage;
    }
}
