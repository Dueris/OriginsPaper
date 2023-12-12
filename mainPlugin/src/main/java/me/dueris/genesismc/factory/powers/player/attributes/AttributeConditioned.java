package me.dueris.genesismc.factory.powers.player.attributes;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BinaryOperator;

public class AttributeConditioned extends CraftPower implements Listener {

    public static void executeAttributeModify(String operation, Attribute attribute_modifier, int base_value, Player p, int value) {
        Map<String, BinaryOperator<Integer>> operationMap = new HashMap<>();
        operationMap.put("addition", Integer::sum);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a + (a * b));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextInt(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextInt(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextInt(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextInt(b));

        BinaryOperator mathOperator = operationMap.get(operation);
        if (mathOperator != null) {
            int result = (int) mathOperator.apply(base_value, value);
            p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).setBaseValue(result);
        } else {
            Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.attribute"));
        }
        p.sendHealthUpdate();
    }

    public static void executeAttributeModify(String operation, Attribute attribute_modifier, int base_value, Player p, Double value) {
        Map<String, BinaryOperator<Double>> operationMap = new HashMap<>();
        operationMap.put("addition", Double::sum);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a + (a * b));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextDouble(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextDouble(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextDouble(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextDouble(b));

        BinaryOperator mathOperator = operationMap.get(operation);
        if (mathOperator != null) {
            double result = (Double) mathOperator.apply(base_value, value);
            p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).setBaseValue(result);
        } else {
            Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.attribute"));
        }
        p.sendHealthUpdate();
    }

    public void executeConditionAttribute(Player p) {
        Map<String, BinaryOperator<Integer>> operationMap = new HashMap<>();
        operationMap.put("addition", Integer::sum);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a + (a * b));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextInt(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextInt(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextInt(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextInt(b));

        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                if (power == null) continue;

                for (HashMap<String, Object> modifier : power.getConditionFromString("modifier", "modifiers")) {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    if (!conditionExecutor.check("condition", "conditions", p, power, "origins:attribute_conditioned", p, null, null, null, p.getItemInHand(), null))
                        return;
                    Attribute attribute_modifier = Attribute.valueOf(modifier.get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase());
                    if (modifier.get("value") instanceof Integer) {
                        int value = Integer.valueOf(modifier.get("value").toString());
                        int base_value = (int) p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).getBaseValue();
                        String operation = String.valueOf(modifier.get("operation"));
                        executeAttributeModify(operation, attribute_modifier, base_value, p, value);
                    } else if (modifier.get("value") instanceof Double) {
                        Double value = Double.valueOf(modifier.get("value").toString());
                        int base_value = (int) p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).getBaseValue();
                        String operation = String.valueOf(modifier.get("operation"));
                        executeAttributeModify(operation, attribute_modifier, base_value, p, value);
                    }
                }
            }

        }
    }

    public void inverseConditionAttribute(Player p) {
        Map<String, BinaryOperator<Integer>> operationMap = new HashMap<>();
        operationMap.put("addition", (a, b) -> a - b);
        operationMap.put("subtraction", Integer::sum);
        operationMap.put("multiplication", (a, b) -> a / b);
        operationMap.put("division", (a, b) -> a * b);
        operationMap.put("multiply_base", (a, b) -> a / (1 + b));
        operationMap.put("multiply_total", (a, b) -> a / (1 + b));
        operationMap.put("set_total", (a, b) -> a);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a - random.nextInt(b));
        operationMap.put("subtract_random_max", (a, b) -> a + random.nextInt(b));
        operationMap.put("multiply_random_max", (a, b) -> a / random.nextInt(b));
        operationMap.put("divide_random_max", (a, b) -> a * random.nextInt(b));

        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                if (power == null) continue;

                for (HashMap<String, Object> modifier : power.getConditionFromString("modifier", "modifiers")) {
                    Attribute attribute_modifier = Attribute.valueOf(modifier.get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase());
                    if (modifier.get("value") instanceof Integer) {
                        int value = Integer.valueOf(modifier.get("value").toString());
                        int base_value = (int) p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).getBaseValue();
                        String operation = String.valueOf(modifier.get("operation"));
                        executeAttributeModify(operation, attribute_modifier, base_value, p, value);
                    } else if (modifier.get("value") instanceof Double) {
                        Double value = Double.valueOf(modifier.get("value").toString());
                        int base_value = (int) p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).getBaseValue();
                        String operation = String.valueOf(modifier.get("operation"));
                        executeAttributeModify(operation, attribute_modifier, base_value, p, value);
                    }
                }
            }

        }
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        applied.put(e.getPlayer(), false);
    }

    HashMap<Player, Boolean> applied = new HashMap<>();

    @Override
    public void run(Player p) {
        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                if (conditioned_attribute.contains(p)) {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    if (conditionExecutor.check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getInventory().getItemInMainHand(), null)) {
                        if (!applied.get(p)) {
                            executeConditionAttribute(p);
                            applied.put(p, true);
                            setActive(power.getTag(), true);
                        }
                    } else {
                        if (applied.get(p)) {
                            inverseConditionAttribute(p);
                            applied.put(p, false);
                            setActive(power.getTag(), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:attribute_conditioned";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return conditioned_attribute;
    }
}
