package me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.events.OriginChangeEvent;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.core.factory.powers.Powers.*;

public class AttributeHandler implements Listener {


    @EventHandler
    public void ExecuteAttributeModification(OriginChangeEvent e) {
        Player p = e.getPlayer();
        if (natural_armor.contains(p)) {
            p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(8);
        }
        if (nine_lives.contains(p)) {
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(18);
        }
        if (attribute.contains(p)) {

            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {

                PowerContainer power = origin.getPowerFileFromType("origins:attribute");
                if (power == null) continue;

                Attribute attribute_modifier = Attribute.valueOf(power.getModifier().get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase());

                if(power.getModifier().get("value") instanceof Integer){
                    int value = Integer.valueOf(power.getModifier().get("value").toString());
                    int base_value = (int) p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).getBaseValue();
                    String operation = String.valueOf(power.getModifier().get("operation"));
                    executeAttributeModify(operation, attribute_modifier, base_value, p, value);
                    if(power.getModifier().get("update_health").toString() != null){
                        if(power.getModifier().get("update_health").toString().equalsIgnoreCase("true")) p.sendHealthUpdate();
                    }
                } else if (power.getModifier().get("value") instanceof Double) {
                    Double value = Double.valueOf(power.getModifier().get("value").toString());
                    int base_value = (int) p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).getBaseValue();
                    String operation = String.valueOf(power.getModifier().get("operation"));
                    executeAttributeModify(operation, attribute_modifier, base_value, p, value);
                    if(power.getModifier().get("update_health").toString() != null){
                        if(power.getModifier().get("update_health").toString().equalsIgnoreCase("true")) p.sendHealthUpdate();
                    }
                }

            }

        }
    }

    public static void executeAttributeModify(String operation, Attribute attribute_modifier, int base_value, Player p, int value){
        Map<String, BinaryOperator<Integer>> operationMap = new HashMap<>();
        //base value = a
        //modifier value = b
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
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to parse origins:attribute, unable to get result");
        }
    }

    public static void executeAttributeModify(String operation, Attribute attribute_modifier, int base_value, Player p, Double value){
        Map<String, BinaryOperator<Integer>> operationMap = new HashMap<>();
        //base value = a
        //modifier value = b
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
            double result = (Double) mathOperator.apply(base_value, value);
            p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).setBaseValue(result);
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to parse origins:attribute, unable to get result");
        }
    }
}
