package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.DataConverter;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.AttributeHandler.appliedAttributes;

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
                    Attribute attributeModifier = DataConverter.resolveAttribute(modifier.handle.getString("attribute"));
                    if (p.getAttribute(attributeModifier) != null && !appliedAttributes.get(p).contains(power)) {
                        double val = DataConverter.convertToAttributeModifier(modifier).getAmount();
                        double baseVal = p.getAttribute(attributeModifier).getBaseValue();
                        String operation = modifier.operation();
                        BinaryOperator<Double> operator = Utils.getOperationMappingsDouble().get(operation);
                        if (operator != null) {
                            double result = Double.parseDouble(String.valueOf(operator.apply(baseVal, val)));
                            p.getAttribute(attributeModifier).setBaseValue(result);
                        } else {
                            Bukkit.getLogger().warning("An unexpected error occurred when retrieving the BinaryOperator for attribute_conditioned!");
                            new Throwable().printStackTrace();
                        }
                    }
                }
            }

        }
    }

    public void inverseConditionAttribute(Player p) {
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                if (power == null) continue;

                for (Modifier modifier : power.getModifiers()) {
                    Attribute attribute_modifier = DataConverter.resolveAttribute(modifier.handle.getString("attribute"));
                    double value = DataConverter.convertToAttributeModifier(modifier).getAmount();
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
        appliedAttributes.putIfAbsent(p, new ArrayList<>());
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
