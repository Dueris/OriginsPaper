package me.dueris.genesismc.factory.powers.player.attributes;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass;
import me.dueris.genesismc.utils.PowerContainer;
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
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    if (executor.check("condition", "conditions", e.getPlayer(), power, getPowerFile(), e.getPlayer(), null, null, null, e.getPlayer().getItemInHand(), null)) {
                        setActive(e.getPlayer(), power.getTag(), true);
                        ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                        applyAttribute(e.getPlayer(), valueModifyingSuperClass.getDefaultValue(power.get("class")), Float.parseFloat(power.get("multiplier", "1.0")), power.get("attribute").toUpperCase().split(":")[1].replace("\\.", "_"));
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
        return "origins:attribute_modify_transfer";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return attribute_modify_transfer;
    }
}
