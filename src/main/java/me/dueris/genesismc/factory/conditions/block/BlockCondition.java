package me.dueris.genesismc.factory.conditions.block;

import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.fluid.FluidCondition;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Fluid;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class BlockCondition implements Condition {
    @Override
    public String condition_type() {
        return "BLOCK_CONDITION";
    }

    public static HashMap<PowerContainer, ArrayList<String>> inTagValues = new HashMap<>();

    @Override
    @SuppressWarnings("index out of bounds")
    public Optional<Boolean> check(HashMap<String, Object> condition, Player p, PowerContainer power, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        if (condition.get("type") == null) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();
        if (type.equals("origins:height")) {
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
            if (RestrictArmor.compareValues(block.getLocation().getY(), comparison, compare_to)) {
                return Optional.of(true);
            }
        }
        if (type.equals("origins:material")) {
            try {
                Material mat = Material.valueOf(condition.get("material").toString().split(":")[1].toUpperCase());
                if (block.getType().equals(mat)) return Optional.of(true);
            } catch (Exception e) {
                //yeah imma fail this silently for some weird out of bounds error
            }
        }
        if (type.equals("origins:in_tag")){
            for(String mat : TagRegistry.getRegisteredTagFromFileKey(condition.get("tag").toString())){
                if(block.getType().equals(Material.valueOf(mat.toString().split(":")[1].toUpperCase()))){
                    return Optional.of(true);
                }
            }
        }
        if (type.equals("origins:adjacent")){
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
            int matchingADJCount = 0;

            for(int xOFF = -1; xOFF <= 1; xOFF++){
                for(int yOFF = -1; yOFF <= 1; yOFF++){
                    for(int zOFF = -1; zOFF <= 1; zOFF++){
                        if(xOFF == 0 && yOFF == 0 && zOFF == 0){
                            continue;
                        }
                        Block adBlock = block.getRelative(xOFF, yOFF, zOFF);
                        BlockCondition blockCondition = new BlockCondition();
                        if(blockCondition.check((HashMap<String, Object>) condition.get("adjacent_condition"), p, power, powerfile, actor, target, adBlock, fluid, itemStack, entityDamageEvent).isPresent() && blockCondition.check(condition, p, power, powerfile, actor, target, adBlock, fluid, itemStack, entityDamageEvent).get()){
                            matchingADJCount++;
                        }
                    }
                }
            }

            return Optional.of(RestrictArmor.compareValues(matchingADJCount, comparison, compare_to));
        }

        if (type.equals("origins:attachable")){
            if(block != null && block.getType() != Material.AIR){
                Block[] adjBlcs = new Block[]{
                        block.getRelative(0, 1, 0), // Up
                        block.getRelative(0, -1, 0), // Down
                        block.getRelative(0, 0, -1), // North
                        block.getRelative(0, 0, 1), // South
                        block.getRelative(-1, 0, 0), // West
                        block.getRelative(1, 0, 0)  // East
                };

                for(Block adj : adjBlcs){
                    if(adj != null && adj.getType().isSolid()){
                        return getResult(inverted, true);
                    }
                }
            }
        }

        if (type.equals("origins:blast_resistance")){
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
            float bR = block.getType().getBlastResistance();
            return getResult(inverted, RestrictArmor.compareValues(bR, comparison, compare_to));
        }

        if (type.equals("origins:block_entity")){
            BlockState blockState = block.getState();
            if(blockState instanceof TileState){
                return getResult(inverted, true);
            }
        }

        if (type.equals("origins:block")){
            if(block.getType().equals(Material.valueOf(condition.get("block").toString().split(":")[1].toUpperCase()))){
                return getResult(inverted, true);
            }
        }

        if (type.equals("origins:exposed_to_sky")){
            if(block.getLightFromSky() > 0){
                return getResult(inverted, true);
            }
        }

        if (type.equals("origins:fluid")){
            FluidCondition fluidCondition = new FluidCondition();
            Optional fl = fluidCondition.check(condition, p, power, powerfile, actor, target, block, fluid, itemStack, entityDamageEvent);
            if(fl.isPresent()){
                if(fl.get().equals(true)){
                    return getResult(inverted, true);
                }
            }
        }

        if (type.equals("origins:hardness")){
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
            float bR = block.getType().getHardness();
            return getResult(inverted, RestrictArmor.compareValues(bR, comparison, compare_to));
        }

        if (type.equals("origins:height")){
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
            float bR = block.getLocation().getBlockY();
            return getResult(inverted, RestrictArmor.compareValues(bR, comparison, compare_to));
        }

        if (type.equals("origins:light_blocking")){
            return getResult(inverted, block.getType().isOccluding());
        }

        if (type.equals("origins:movement_blocking")){
            return getResult(inverted, block.getType().isCollidable());
        }

        if (type.equals("origins:replaceable")){
            return getResult(inverted, block.getType().isAir() || block.isReplaceable());
        }

        if (type.equals("origins:water_loggable")){
            return getResult(inverted, block.getBlockData().getAsString().contains("waterlogged"));
        }

        return getResult(inverted, false);
    }
}
