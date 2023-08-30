package me.dueris.genesismc.factory.powers.player.attributes;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class AttributeModifyTransfer extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }


    @Override
    public void run() {

    }

    @EventHandler
    public void runChange(OriginChangeEvent e) {
        if (getPowerArray().contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                ConditionExecutor executor = new ConditionExecutor();
                if (executor.check("condition", "conditions", e.getPlayer(), origin, getPowerFile(), e.getPlayer(), null, null, null, e.getPlayer().getItemInHand(), null)) {
                    if (!getPowerArray().contains(e.getPlayer())) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                    ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                    applyAttribute(e.getPlayer(), valueModifyingSuperClass.getDefaultValue(origin.getPowerFileFromType(getPowerFile()).get("class")), Float.parseFloat(origin.getPowerFileFromType(getPowerFile()).get("multiplier", "1.0")), origin.getPowerFileFromType(getPowerFile()).get("attribute").toUpperCase().split(":")[1].replace("\\.", "_"));
                } else {
                    if (!getPowerArray().contains(e.getPlayer())) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
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
