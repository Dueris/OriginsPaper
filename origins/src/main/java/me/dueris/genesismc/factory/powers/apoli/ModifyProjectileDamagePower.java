package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
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
                            if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p) && ConditionExecutor.testEntity(power.get("target_condition"), (CraftEntity) e.getEntity()) && ConditionExecutor.testDamage(power.get("damage_condition"), e)) {
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
