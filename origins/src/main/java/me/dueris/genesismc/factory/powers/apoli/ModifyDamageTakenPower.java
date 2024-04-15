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
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_damage_taken;

public class ModifyDamageTakenPower extends CraftPower implements Listener {


    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void damageEVENT(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p && modify_damage_taken.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                try {
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) continue;
                        if (e instanceof EntityDamageByEntityEvent ev) {
                            if (!ConditionExecutor.testBiEntity(power.getJsonObject("bientity_condition"), (CraftEntity) ev.getDamager(), (CraftEntity) p))
                                continue;
                        }
                        if (!ConditionExecutor.testDamage(power.getJsonObject("damage_condition"), e)) continue;
                        for (Modifier modifier : power.getModifiers()) {
                            float value = modifier.value();
                            String operation = modifier.operation();
                            runSetDMG(e, operation, value);
                            if (e instanceof EntityDamageByEntityEvent ev) {
                                Actions.executeBiEntity(ev.getDamager(), p, power.getJsonObject("bientity_action"));
                                Actions.executeEntity(ev.getDamager(), power.getJsonObject("attacker_action"));
                            }
                            Actions.executeEntity(p, power.getJsonObject("self_action"));
                            setActive(p, power.getTag(), true);
                        }
                    }
                } catch (Exception ev) {
//                    throw new RuntimeException();
                }
            }
        }
    }

    public void runSetDMG(EntityDamageEvent e, String operation, Object value) {
        double damage = e.getDamage();

        BinaryOperator<Float> floatOperator = Utils.getOperationMappingsFloat().get(operation);
        if (floatOperator != null) {
            float newDamage = floatOperator.apply((float) damage, (Float) value);
            e.setDamage(newDamage);
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_damage_taken";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_damage_taken;
    }
}
