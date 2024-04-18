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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_damage_dealt;

public class ModifyDamageDealtPower extends CraftPower implements Listener {


    @Override
    public void run(Player p) {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damageEVENT(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        if (e.getDamager() instanceof Player p && modify_damage_dealt.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                try {
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) continue;
                        if (!ConditionExecutor.testEntity(power.getJsonObject("target_condition"), (CraftEntity) e.getEntity()))
                            continue;
                        if (!ConditionExecutor.testBiEntity(power.getJsonObject("bientity_condition"), (CraftEntity) p, (CraftEntity) e.getEntity()))
                            continue;
                        if (!ConditionExecutor.testDamage(power.getJsonObject("damage_condition"), e)) continue;
                        for (Modifier modifier : power.getModifiers()) {
                            float value = modifier.value();
                            String operation = modifier.operation();
                            runSetDMG(e, operation, value);
                            setActive(p, power.getTag(), true);
                        }
                        Actions.executeBiEntity(p, e.getEntity(), power.getJsonObject("bientity_action"));
                        Actions.executeEntity(e.getEntity(), power.getJsonObject("target_action"));
                        Actions.executeEntity(p, power.getJsonObject("self_action"));
                    }
                } catch (Exception ev) {
                    // throw new RuntimeException(); // urm why?
                }
            }
        }
    }

    public void runSetDMG(EntityDamageByEntityEvent e, String operation, float value) {
        double damage = e.getDamage();
        BinaryOperator<Float> floatOperator = Utils.getOperationMappingsFloat().get(operation);
        if (floatOperator != null) {
            float newDamage = floatOperator.apply((float) damage, value);
            e.setDamage(newDamage);
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_damage_dealt";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_damage_dealt;
    }
}
