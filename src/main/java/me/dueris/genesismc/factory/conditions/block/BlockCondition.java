package me.dueris.genesismc.factory.conditions.block;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.CraftCondition;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class BlockCondition implements Condition {
    @Override
    public String condition_type() {
        return "BLOCK_CONDITION";
    }
    public static HashMap<PowerContainer, ArrayList<String>> inTagValues = new HashMap<>();

    @Override
    @SuppressWarnings("index out of bounds")
    public Optional<Boolean> check(HashMap<String, Object> condition, Player p, OriginContainer origin, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        p.sendMessage("stest");
        if (origin == null) return Optional.empty();
        if (origin.getPowerFileFromType(powerfile) == null) return Optional.empty();
            if (condition.isEmpty()) return Optional.empty();
            if (condition.get("type") == null) return Optional.empty();
            boolean inverted = (boolean) condition.getOrDefault("inverted", false);
            String type = condition.get("type").toString().toLowerCase();
            p.sendMessage(type);
            if(type.equals("origins:height")){
                p.sendMessage("SDHFSKDJJFSDFS");
                String comparison = condition.get("comparison").toString();
                float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                if(RestrictArmor.compareValues(block.getLocation().getY(), comparison, compare_to)){
                    return Optional.of(true);
                }
            }
            if(type.equals("origins:material")){
                try{
                    Material mat = Material.valueOf(condition.get("material").toString().split(":")[1].toUpperCase());
                    if(block.getType().equals(mat)) return Optional.of(true);
                }catch (Exception e){
                    //yeah imma fail this silently for some weird out of bounds error
                }
            }

            return getResult(inverted, false);
    }
}
