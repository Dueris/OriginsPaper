package me.dueris.genesismc.factory.conditions.block;

import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.fluid.FluidCondition;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
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
    public static HashMap<PowerContainer, ArrayList<String>> inTagValues = new HashMap<>();
    public static HashMap<String, ArrayList<Material>> blockTagMappings = new HashMap<>();

    @Override
    public String condition_type() {
        return "BLOCK_CONDITION";
    }

    @Override
    @SuppressWarnings("index out of bounds")
    public Optional<Boolean> check(JSONObject condition, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty() || condition == null) return Optional.empty();
        if (condition.get("type") == null) return Optional.empty();
        if (block == null) return Optional.empty();
        if (block.getType() == null) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();
        switch (type) {
            case "origins:material" -> {
                try {
                    String matSplit = condition.get("material").toString().toUpperCase();
                    if (matSplit.contains(":")) {
                        matSplit = matSplit.split(":")[1];
                    }
                    Material mat = Material.valueOf(matSplit);
                    return Optional.of(block.getType().equals(mat));
                } catch (Exception e) {
                    e.printStackTrace();
                    //yeah imma fail this silently for some weird out of bounds error
                }
            }
            case "origins:in_tag" -> {
                if (TagRegistry.getRegisteredTagFromFileKey(condition.get("tag").toString()) != null) {
                    if (!blockTagMappings.containsKey(condition.get("tag"))) {
                        blockTagMappings.put(condition.get("tag").toString(), new ArrayList<>());
                        for (String mat : TagRegistry.getRegisteredTagFromFileKey(condition.get("tag").toString())) {
                            blockTagMappings.get(condition.get("tag")).add(Material.valueOf(mat.split(":")[1].toUpperCase()));
                        }
                    } else {
                        // mappings exist, now we can start stuff
                        return getResult(inverted, Optional.of(blockTagMappings.get(condition.get("tag")).contains(block.getType())));
                    }
                } else {
                    return getResult(inverted, Optional.of(false));
                }
            }
//            case "origins:adjacent" -> {
//                String comparison = condition.get("comparison").toString();
//                float compare_to = Float.parseFloat(condition.get("compare_to").toString());
//                int matchingADJCount = 0;
//
//                for(int xOFF = -1; xOFF <= 1; xOFF++){
//                    for(int yOFF = -1; yOFF <= 1; yOFF++){
//                        for(int zOFF = -1; zOFF <= 1; zOFF++){
//                            if(xOFF == 0 && yOFF == 0 && zOFF == 0){
//                                continue;
//                            }
//                            if(condition.get("adjacent_condition") != null){
//                                Block adBlock = block.getRelative(xOFF, yOFF, zOFF);
//                                BlockCondition blockCondition = ConditionExecutor.blockCondition;
//                                if(blockCondition.check((HashMap<String, Object>) condition.get("adjacent_condition"), p, power, powerfile, actor, target, adBlock, fluid, itemStack, entityDamageEvent).isPresent() && blockCondition.check(condition, p, power, powerfile, actor, target, adBlock, fluid, itemStack, entityDamageEvent).get()){
//                                    matchingADJCount++;
//                                }
//                            }else{
//                                matchingADJCount++;
//                            }
//                        }
//                    }
//                }
//
//                return Optional.of(RestrictArmor.compareValues(matchingADJCount, comparison, compare_to));
//            }
            case "origins:attachable" -> {
                if (block != null && block.getType() != Material.AIR) {
                    Block[] adjBlcs = new Block[]{
                            block.getRelative(0, 1, 0), // Up
                            block.getRelative(0, -1, 0), // Down
                            block.getRelative(0, 0, -1), // North
                            block.getRelative(0, 0, 1), // South
                            block.getRelative(-1, 0, 0), // West
                            block.getRelative(1, 0, 0)  // East
                    };

                    for (Block adj : adjBlcs) {
                        return getResult(inverted, Optional.of(adj != null && adj.getType().isSolid()));
                    }
                } else {
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "origins:blast_resistance" -> {
                String comparison = condition.get("comparison").toString();
                float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                float bR = block.getType().getBlastResistance();
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(bR, comparison, compare_to)));
            }
            case "origins:block_entity" -> {
                BlockState blockState = block.getState();
                return getResult(inverted, Optional.of(blockState instanceof TileState));
            }
            case "origins:block" -> {
                return getResult(inverted, Optional.of(block.getType().equals(Material.valueOf(condition.get("block").toString().split(":")[1].toUpperCase()))));
            }
            case "origins:exposed_to_sky" -> {
                return getResult(inverted, Optional.of(block.getLightFromSky() > 0));
            }
            case "origins:fluid" -> {
                FluidCondition fluidCondition = new FluidCondition();
                Optional fl = fluidCondition.check(condition, actor, target, block, fluid, itemStack, entityDamageEvent);
                if (fl.isPresent()) {
                    return getResult(inverted, Optional.of(fl.get().equals(true)));
                } else {
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "origins:hardness" -> {
                String comparison = condition.get("comparison").toString();
                float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                float bR = block.getType().getHardness();
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(bR, comparison, compare_to)));
            }
            case "origins:height" -> {
                String comparison = condition.get("comparison").toString();
                float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                float bR = block.getLocation().getBlockY();
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(bR, comparison, compare_to)));
            }
            case "origins:light_blocking" -> {
                return getResult(inverted, Optional.of(block.getType().isOccluding()));
            }
            case "origins:light_level" -> {
                String lightType = condition.get("light_type").toString();
                CraftBlock bl = (CraftBlock) block;
                int level = 0;
                switch (lightType) {
                    case "sky" -> {
                        level = bl.getLightFromSky();
                    }
                    case "block" -> {
                        level = bl.getLightFromBlocks();
                    }
                    default -> {
                        level = bl.getLightLevel();
                    }
                }

                String comparison = condition.get("comparison").toString();
                float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                float bR = level;
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(bR, comparison, compare_to)));
            }
            case "origins:movement_blocking" -> {
                return getResult(inverted, Optional.of(block.getType().isCollidable()));
            }
            case "origins:replacable" -> {
                return getResult(inverted, Optional.of(block.getType().isAir() || block.isReplaceable()));
            }
            case "origins:water_loggable" -> {
                return getResult(inverted, Optional.of(block.getBlockData().getAsString().contains("waterlogged")));
            }
            default -> {
                return getResult(inverted, Optional.empty());
            }
        }

        return getResult(inverted, Optional.empty());
    }
}
