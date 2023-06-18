package me.dueris.genesismc.core.factory.powers.attributes;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.core.factory.powers.Powers.attribute;
import static me.dueris.genesismc.core.factory.powers.Powers.conditioned_attribute;

public class AttributeConditioned implements Listener {

    public static void executeConditionAttribute(Player p){
        Map<String, BinaryOperator<Integer>> operationMap = new HashMap<>();
        //base value = a
        //modifier value = b
        operationMap.put("addition", Integer::sum);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a + (a * b));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> a - a + b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextInt(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextInt(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextInt(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextInt(b));

        if(OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute") == null) return;

        Attribute attribute_modifier = Attribute.valueOf(OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getModifier().get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase());
        int value = Integer.valueOf(OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getModifier().get("value").toString());
        String operation = String.valueOf(OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getModifier().get("operation"));
        int base_value = (int) p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).getBaseValue();

        BinaryOperator mathOperator = operationMap.get(operation);
        if(mathOperator != null) {
            int result = (int) mathOperator.apply(base_value, value);
            p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).setBaseValue(result);
        }else{
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to parse origins:attribute, unable to get result");
        }
    }

    public static void inverseConditionAttribute(Player p){
        Map<String, BinaryOperator<Integer>> operationMap = new HashMap<>();
        //base value = a
        //modifier value = b
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

        if(OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute") == null) return;

        Attribute attribute_modifier = Attribute.valueOf(OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getModifier().get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase());
        int value = Integer.valueOf(OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getModifier().get("value").toString());
        String operation = String.valueOf(OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getModifier().get("operation"));
        int base_value = (int) p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).getBaseValue();

        BinaryOperator mathOperator = operationMap.get(operation);
        if(mathOperator != null) {
            int result = (int) mathOperator.apply(base_value, value);
            p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).setBaseValue(result);
        }else{
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to parse origins:conditioned_attribute, unable to get result");
        }
    }

    @EventHandler
    public void ExecuteSprintCondition(PlayerToggleSprintEvent e){
        Player p = e.getPlayer();
        if(e.isSprinting()){
            if(conditioned_attribute.contains(p)){
                if(!OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getConditions().get("type").toString().equalsIgnoreCase("origins:sprinting")) return;
                executeConditionAttribute(p);
            }
        }else{
            if(conditioned_attribute.contains(p)){
                if(!OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getConditions().get("type").toString().equalsIgnoreCase("origins:sprinting")) return;
                inverseConditionAttribute(p);
            }
        }
    }
    @EventHandler
    public void ExecuteFlightCondition(PlayerToggleFlightEvent e){
        Player p = e.getPlayer();
        if(e.isFlying()){
            if(conditioned_attribute.contains(p)){
                if(!OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getConditions().get("type").toString().equalsIgnoreCase("origins:flying")) return;
                executeConditionAttribute(p);
            }
        }else{
            if(conditioned_attribute.contains(p)){
                if(!OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getConditions().get("type").toString().equalsIgnoreCase("origins:flying")) return;
                inverseConditionAttribute(p);
            }
        }
    }
    @EventHandler
    public void ExecuteGlideCondition(EntityToggleGlideEvent e){
        if(!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if(e.isGliding()){
            if(conditioned_attribute.contains(p)){
                if(!OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getConditions().get("type").toString().equalsIgnoreCase("origins:gliding")) return;
                executeConditionAttribute(p);
            }
        }else{
            if(conditioned_attribute.contains(p)){
                if(!OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getConditions().get("type").toString().equalsIgnoreCase("origins:gliding")) return;
                inverseConditionAttribute(p);
            }
        }
    }
    @EventHandler
    public void ExecuteSneakCondition(PlayerToggleSneakEvent e){
        Player p = e.getPlayer();
        if(e.isSneaking()){
            if(conditioned_attribute.contains(p)){
                if(!OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getConditions().get("type").toString().equalsIgnoreCase("origins:sneaking")) return;
                executeConditionAttribute(p);
            }
        }else{
            if(conditioned_attribute.contains(p)){
                if(!OriginPlayer.getOrigin(p).getPowerFileFromType("origins:conditioned_attribute").getConditions().get("type").toString().equalsIgnoreCase("origins:sneaking")) return;
                inverseConditionAttribute(p);
            }
        }
    }


}
