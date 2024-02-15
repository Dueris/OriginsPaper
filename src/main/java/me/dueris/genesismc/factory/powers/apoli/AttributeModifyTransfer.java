package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

public class AttributeModifyTransfer extends CraftPower implements Listener {

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
    public void runChange(OriginChangeEvent e) {
        if (getPowerArray().contains(e.getPlayer())) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    if (executor.check("condition", "conditions", e.getPlayer(), power, getPowerFile(), e.getPlayer(), null, null, null, e.getPlayer().getItemInHand(), null)) {
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
