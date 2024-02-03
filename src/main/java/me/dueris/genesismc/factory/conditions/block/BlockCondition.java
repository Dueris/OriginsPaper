package me.dueris.genesismc.factory.conditions.block;

import com.mojang.brigadier.StringReader;
import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.fluid.FluidCondition;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.Utils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.LiquidBlockContainer;
import org.bukkit.Bukkit;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
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
            case "apoli:material" -> {
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
            case "apoli:in_tag" -> {
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
            case "apoli:adjacent" -> {
                BlockCondition adjCon = new BlockCondition();
                int adj = 0;
                for(Direction direction : Direction.values()){
                    Optional<Boolean> conOp = adjCon.check((JSONObject) condition.get("adjacent_condition"), actor, target, block.getWorld().getBlockAt(CraftLocation.toBukkit(((CraftBlock)block).getPosition().offset(direction.getNormal()))), fluid, itemStack, entityDamageEvent);
                    boolean add = false;
                    if(!conOp.isPresent()){
                        add = true;
                    }
                    if(conOp.isPresent() && conOp.get()){
                        add = true;
                    }
                    if(add){
                        adj++;
                    }
                }
                String comparison = condition.get("comparison").toString();
                float compare_to = Float.parseFloat(condition.get("compare_to").toString());

                return getResult(inverted, Optional.of(RestrictArmor.compareValues(adj, comparison, compare_to)));
            }
            case "apoli:attachable" -> {
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
            case "apoli:blast_resistance" -> {
                String comparison = condition.get("comparison").toString();
                float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                float bR = block.getType().getBlastResistance();
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(bR, comparison, compare_to)));
            }
            case "apoli:block_entity" -> {
                BlockState blockState = block.getState();
                return getResult(inverted, Optional.of(blockState instanceof TileState));
            }
            case "apoli:block" -> {
                return getResult(inverted, Optional.of(block.getType().equals(Material.valueOf(condition.get("block").toString().split(":")[1].toUpperCase()))));
            }
            case "apoli:exposed_to_sky" -> {
                return getResult(inverted, Optional.of(block.getLightFromSky() > 0));
            }
            case "apoli:fluid" -> {
                FluidCondition fluidCondition = new FluidCondition();
                Optional fl = fluidCondition.check(condition, actor, target, block, fluid, itemStack, entityDamageEvent);
                if (fl.isPresent()) {
                    return getResult(inverted, Optional.of(fl.get().equals(true)));
                } else {
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "apoli:hardness" -> {
                String comparison = condition.get("comparison").toString();
                float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                float bR = block.getType().getHardness();
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(bR, comparison, compare_to)));
            }
            case "apoli:height" -> {
                String comparison = condition.get("comparison").toString();
                float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                float bR = block.getLocation().getBlockY();
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(bR, comparison, compare_to)));
            }
            case "apoli:light_blocking" -> {
                return getResult(inverted, Optional.of(block.getType().isOccluding()));
            }
            case "apoli:light_level" -> {
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
            case "apoli:slipperiness" -> {
                String comparison = condition.get("comparison").toString();
                float compare_to = Float.parseFloat(condition.get("compare_to").toString());
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(((CraftBlock)block).getBlockData().getMaterial().getSlipperiness(), comparison, compare_to)));
            }
            case "apoli:movement_blocking" -> {
                return getResult(inverted, Optional.of(block.getType().isCollidable()));
            }
            case "apoli:replacable" -> {
                return getResult(inverted, Optional.of(block.getType().isAir() || block.isReplaceable()));
            }
            case "apoli:water_loggable" -> {
                return getResult(inverted, Optional.of(((CraftBlock)block).getHandle().getBlockState(((CraftBlock)block).getPosition()).getBlock() instanceof LiquidBlockContainer));
            }
            default -> {
                return getResult(inverted, Optional.empty());
            }
        }

        return getResult(inverted, Optional.empty());
    }
}
