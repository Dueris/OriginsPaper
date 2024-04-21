package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

public class AttributeConditioned extends CraftPower implements Listener {

    private static final HashMap<Player, Boolean> applied = new HashMap<>();

    public static void executeAttributeModify(String operation, Attribute attribute_modifier, double base_value, Player p, Double value) {
        BinaryOperator<Double> operator = Utils.getOperationMappingsDouble().get(operation);
        if (operator != null) {
            double result = Double.parseDouble(String.valueOf(operator.apply(base_value, value)));
            p.getAttribute(attribute_modifier).setBaseValue(result);
        } else {
            Bukkit.getLogger().warning("An unexpected error occurred when retrieving the BinaryOperator for attribute_conditioned!");
            new Throwable().printStackTrace();
        }
        p.sendHealthUpdate();
    }

    public void executeConditionAttribute(Player p) {
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                if (power == null) continue;
                for (Modifier modifier : power.getModifiers()) {
                    if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) return;
                    Attribute attribute_modifier = Attribute.valueOf(NamespacedKey.fromString(modifier.handle.getString("attribute")).asString().split(":")[1].replace(".", "_").toUpperCase());
                    double val = modifier.value();
                    double baseVal = p.getAttribute(attribute_modifier).getBaseValue();
                    String operation = modifier.operation();
                    BinaryOperator<Double> operator = Utils.getOperationMappingsDouble().get(operation);
                    if (operator != null) {
                        double result = Double.parseDouble(String.valueOf(operator.apply(baseVal, val)));
                        p.getAttribute(attribute_modifier).setBaseValue(result);
                    } else {
                        Bukkit.getLogger().warning("An unexpected error occurred when retrieving the BinaryOperator for attribute_conditioned!");
                        new Throwable().printStackTrace();
                    }
                    p.sendHealthUpdate();
                }
            }

        }
    }

    public void inverseConditionAttribute(Player p) {
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                if (power == null) continue;

                for (Modifier modifier : power.getModifiers()) {
                    Attribute attribute_modifier = Attribute.valueOf(NamespacedKey.fromString(modifier.handle.getString("attribute")).asString().split(":")[1].replace(".", "_").toUpperCase());
                    double value = modifier.value();
                    double base_value = p.getAttribute(attribute_modifier).getBaseValue();
                    String operation = modifier.operation();
                    executeAttributeModify(operation, attribute_modifier, base_value, p, -value);
                }
            }

        }
    }


    @EventHandler
    public void join(PlayerJoinEvent e) {
        applied.put(e.getPlayer(), false);
    }

    @Override
    public void run(Player p, Power power) {
        if (!applied.containsKey(p)) {
            applied.put(p, false);
        }
        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
            if (!applied.get(p)) {
                executeConditionAttribute(p);
                applied.put(p, true);
                setActive(p, power.getTag(), true);
            }
        } else {
            if (applied.get(p)) {
                inverseConditionAttribute(p);
                applied.put(p, false);
                setActive(p, power.getTag(), false);
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:conditioned_attribute";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return conditioned_attribute;
    }
}
