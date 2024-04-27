package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_projectile_damage;

public class ModifyProjectileDamagePower extends CraftPower implements Listener {

    @EventHandler
    public void runD(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Projectile p && p.getShooter() instanceof Player pl) {
            if (modify_projectile_damage.contains(pl)) {
                for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                    try {
                        for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(pl, getType(), layer)) {
                            if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p) && ConditionExecutor.testEntity(power.getJsonObject("target_condition"), (CraftEntity) e.getEntity()) && ConditionExecutor.testDamage(power.getJsonObject("damage_condition"), e)) {
                                for (Modifier modifier : power.getModifiers()) {
                                    float value = modifier.value();
                                    String operation = modifier.operation();
                                    BinaryOperator<Double> mathOperator = Utils.getOperationMappingsDouble().get(operation);
                                    if (mathOperator != null) {
                                        ModifyDamageDealtPower damageDealtPower = new ModifyDamageDealtPower();
                                        damageDealtPower.runSetDMG(e, operation, value);
                                        setActive(pl, power.getTag(), true);
                                        Actions.executeEntity(e.getEntity(), power.getJsonObject("target_action"));
                                        Actions.executeEntity(pl, power.getJsonObject("self_action"));
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
    public String getType() {
        return "apoli:modify_projectile_damage";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return modify_projectile_damage;
    }
}
