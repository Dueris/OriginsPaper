package me.dueris.genesismc.factory.powers.player.attributes;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.translation.LangConfig;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BinaryOperator;

public class AttributeConditioned extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

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
            Bukkit.getLogger().warning(LangConfig.getLocalizedString(p,"powers.errors.attribute"));
        }
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
    }

    public static void executeConditionAttribute(Player p) {
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

            PowerContainer power = origin.getPowerFileFromType("origins:attribute");
            if (power == null) continue;

            for(HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:attribute_conditioned").getConditionFromString("modifier", "modifiers")){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if(!conditionExecutor.check("condition", "conditions", p, origin, "origins:attribute_conditioned", null, p)) return;
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

    public static void inverseConditionAttribute(Player p) {
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

            PowerContainer power = origin.getPowerFileFromType("origins:attribute_conditioned");
            if (power == null) continue;

            for(HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:attribute_conditioned").getConditionFromString("modifier", "modifiers")){
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

    @EventHandler
    public void ExecuteSprintCondition(PlayerToggleSprintEvent e) {
        Player p = e.getPlayer();
        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            if (conditioned_attribute.contains(p)) {
                if (!origin.getPowerFileFromType("origins:conditioned_attribute").getCondition().get("type").toString().equalsIgnoreCase("origins:sprinting"))
                    return;
                if (e.isSprinting()) {
                    executeConditionAttribute(p);
                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                } else {
                    inverseConditionAttribute(p);
                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }
            }
        }
    }

    @EventHandler
    public void ExecuteFlightCondition(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            if (conditioned_attribute.contains(p)) {
                if (!origin.getPowerFileFromType("origins:conditioned_attribute").getCondition().get("type").toString().equalsIgnoreCase("origins:flying"))
                    return;
                if (e.isFlying()) {
                    executeConditionAttribute(p);
                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                } else {
                    inverseConditionAttribute(p);
                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }
            }
        }
    }

    @EventHandler
    public void ExecuteGlideCondition(EntityToggleGlideEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            if (conditioned_attribute.contains(p)) {
                if (!origin.getPowerFileFromType("origins:conditioned_attribute").getCondition().get("type").toString().equalsIgnoreCase("origins:gliding"))
                    return;
                if (e.isGliding()) {
                    executeConditionAttribute(p);
                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                } else {
                    inverseConditionAttribute(p);
                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }
            }
        }
    }

    @EventHandler
    public void ExecuteSneakCondition(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            if (conditioned_attribute.contains(p)) {
                if (!origin.getPowerFileFromType("origins:conditioned_attribute").getCondition().get("type").toString().equalsIgnoreCase("origins:sneaking"))
                    return;
                if (e.isSneaking()) {
                    executeConditionAttribute(p);
                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                } else {
                    inverseConditionAttribute(p);
                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }
            }
        }
    }

    @Override
    public void run() {

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
