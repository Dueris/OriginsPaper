package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class AttributeModifyTransfer extends CraftPower implements Listener {


    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void runChange(OriginChangeEvent e) {
        if (getPowerArray().contains(e.getPlayer())) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power, power.get("condition"), (CraftEntity) e.getPlayer())) {
                        setActive(e.getPlayer(), power.getTag(), true);
                        ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                        applyAttribute(e.getPlayer(), valueModifyingSuperClass.getDefaultValue(power.getString("class")), power.getFloatOrDefault("multiplier", 1.0f), power.getString("attribute").toUpperCase().split(":")[1].replace("\\.", "_"));
                    } else {
                        setActive(e.getPlayer(), power.getTag(), false);
                    }
                }
            }
        }
    }

    public void applyAttribute(Player p, float value, float multiplier, String attribute) {
        p.getAttribute(Attribute.valueOf(attribute)).setBaseValue(value * multiplier);
    }

    @Override
    public String getPowerFile() {
        return "apoli:attribute_modify_transfer";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return attribute_modify_transfer;
    }
}
