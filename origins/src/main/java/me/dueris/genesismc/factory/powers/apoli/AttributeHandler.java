package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import me.dueris.genesismc.event.AttributeExecuteEvent;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.screen.OriginPage;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

public class AttributeHandler extends CraftPower implements Listener {

    public static void executeAttributeModify(String operation, Attribute attribute_modifier, double base_value, Player p, double value) {
        BinaryOperator<Double> mathOperator = Utils.getOperationMappingsDouble().get(operation);
        if (mathOperator != null) {
            double result = mathOperator.apply(base_value, value);
            p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).setBaseValue(result);
        } else {
            Bukkit.getLogger().warning("An unexpected error occurred when retrieving the BinaryOperator for attribute_conditioned!");
            new Throwable().printStackTrace();
        }
    }

    @EventHandler
    public void powerUpdate(PowerUpdateEvent e) {
        if (!e.getPower().getType().equalsIgnoreCase(this.getType())) return;
        Player p = e.getPlayer();
        OriginPage.setAttributesToDefault(p);
        if (attribute.contains(p)) {
            runAttributeModifyPower(e);
        }
    }

    @EventHandler
    public void respawn(PlayerPostRespawnEvent e) {
        Player p = e.getPlayer();
        OriginPage.setAttributesToDefault(p);
        if (attribute.contains(p)) {
            runAttributeModifyPower(e);
        }
    }

    protected void runAttributeModifyPower(PlayerEvent e) {
        Player p = e.getPlayer();
        if (!attribute.contains(p)) return;
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                if (power == null) continue;

                for (Modifier modifier : power.getModifiers()) {
                    try {
                        Attribute attribute_modifier = Attribute.valueOf(NamespacedKey.fromString(modifier.handle.getString("attribute")).asString().split(":")[1].replace(".", "_").toUpperCase());

                        float value = modifier.value();
                        double base_value = p.getAttribute(attribute_modifier).getBaseValue();
                        String operation = modifier.operation();
                        executeAttributeModify(operation, attribute_modifier, base_value, p, value);
                        AttributeExecuteEvent attributeExecuteEvent = new AttributeExecuteEvent(p, attribute_modifier, power, e.isAsynchronous());
                        Bukkit.getServer().getPluginManager().callEvent(attributeExecuteEvent);
                        setActive(p, power.getTag(), true);
                        p.sendHealthUpdate();
                    } catch (Exception ev) {
                        ev.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:attribute";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return attribute;
    }
}
